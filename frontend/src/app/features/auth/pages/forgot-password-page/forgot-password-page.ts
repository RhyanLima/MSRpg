import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, OnDestroy, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ValidationErrors, Validators } from '@angular/forms';
import { API_URL } from '../../../../core/services/api.service';

const SPECIAL_RE = /[!@#$%^&*()\-_=+[\]{};':"\\|,.<>/?]/;
const OTP_LENGTH = 6;
const TIMER_SECONDS = 10 * 60;
const RESEND_COOLDOWN = 60;

function passwordStrengthValidator(c: AbstractControl): ValidationErrors | null {
  const v: string = c.value ?? '';
  if (!v) return null;
  const e: ValidationErrors = {};
  if (v.length < 8) e['minLength'] = true;
  if (!/[A-Z]/.test(v)) e['uppercase'] = true;
  if (!/[a-z]/.test(v)) e['lowercase'] = true;
  if (!/\d/.test(v)) e['number'] = true;
  if (!SPECIAL_RE.test(v)) e['special'] = true;
  return Object.keys(e).length ? e : null;
}

function passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
  const pwd = group.get('newPassword')?.value;
  const conf = group.get('confirmPassword')?.value;
  return pwd && conf && pwd !== conf ? { mismatch: true } : null;
}

type Step = 'request' | 'otp' | 'newpass' | 'done';

@Component({
  selector: 'app-forgot-password-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './forgot-password-page.html',
  styleUrl: './forgot-password-page.scss',
})
export class ForgotPasswordPage implements OnDestroy {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = inject(API_URL);
  private readonly fb = inject(FormBuilder);

  readonly step = signal<Step>('request');
  readonly stepIndex = computed(() => ({ request: 0, otp: 1, newpass: 2, done: 3 }[this.step()]));

  /* Step 1 */
  readonly emailCtrl = this.fb.control('', [Validators.required, Validators.email]);
  readonly requestLoading = signal(false);
  readonly requestError = signal<string | null>(null);

  /* Step 2 OTP */
  readonly otpDigits = signal<string[]>(Array(OTP_LENGTH).fill(''));
  readonly otpVerifying = signal(false);
  readonly otpHasError = signal(false);
  readonly otpError = signal<string | null>(null);
  readonly timerSeconds = signal(TIMER_SECONDS);
  readonly resendCooldown = signal(0);
  readonly otpIndices = [0, 1, 2, 3, 4, 5];

  private timerIntervalId?: ReturnType<typeof setInterval>;
  private resendIntervalId?: ReturnType<typeof setInterval>;

  readonly maskedEmail = computed(() => {
    const e = this.emailCtrl.value ?? '';
    const [local, domain] = e.split('@');
    if (!domain) return e;
    return `${local[0]}***@${domain}`;
  });

  readonly timerDisplay = computed(() => {
    const s = this.timerSeconds();
    return `${String(Math.floor(s / 60)).padStart(2, '0')}:${String(s % 60).padStart(2, '0')}`;
  });

  readonly canVerifyOtp = computed(() => this.otpDigits().every(d => d !== '') && !this.otpVerifying());
  readonly currentOtpCode = computed(() => this.otpDigits().join(''));

  /* Step 3 */
  readonly newPassForm = this.fb.group(
    {
      newPassword: ['', [Validators.required, passwordStrengthValidator]],
      confirmPassword: ['', Validators.required],
    },
    { validators: passwordMatchValidator },
  );

  readonly showNewPass = signal(false);
  readonly showConfirm = signal(false);
  readonly savingPass = signal(false);
  readonly newPassError = signal<string | null>(null);

  readonly pwdScore = computed(() => {
    const v = this.newPassForm.controls.newPassword.value ?? '';
    return [v.length >= 8, /[A-Z]/.test(v), /[a-z]/.test(v), /\d/.test(v), SPECIAL_RE.test(v)].filter(Boolean).length;
  });

  readonly pwdLevelText = computed(() => (['', 'Fraca', 'Razoável', 'Boa', 'Forte'][this.pwdScore()] ?? ''));
  readonly pwdLevelColor = computed(() => (['', '#c0392b', '#c9922a', '#8bc34a', '#4caf7d'][this.pwdScore()] ?? ''));

  pwdBarClass(index: number): string {
    const score = this.pwdScore();
    if (index >= score) return 'pw-strength__bar';
    return `pw-strength__bar ${(['active-weak', 'active-fair', 'active-strong', 'active-strong'])[Math.min(score - 1, 3)]}`;
  }

  ngOnDestroy(): void {
    this.clearTimers();
  }

  private clearTimers(): void {
    if (this.timerIntervalId) clearInterval(this.timerIntervalId);
    if (this.resendIntervalId) clearInterval(this.resendIntervalId);
  }

  private startTimer(): void {
    this.clearTimers();
    this.timerSeconds.set(TIMER_SECONDS);
    this.timerIntervalId = setInterval(() => {
      const r = this.timerSeconds() - 1;
      this.timerSeconds.set(r);
      if (r <= 0) {
        clearInterval(this.timerIntervalId);
        this.otpError.set('Código expirado. Solicite um novo código.');
      }
    }, 1000);
  }

  private get emailValue(): string {
    return (this.emailCtrl.value ?? '').trim().toLowerCase();
  }

  onSendRequest(): void {
    if (this.emailCtrl.invalid) return;
    this.requestLoading.set(true);
    this.requestError.set(null);

    this.http.post<void>(`${this.apiUrl}/email-verification/password-reset/send`, { email: this.emailValue })
      .subscribe({
        next: () => { this.requestLoading.set(false); this.step.set('otp'); this.startTimer(); },
        error: (err: HttpErrorResponse) => {
          this.requestLoading.set(false);
          this.requestError.set(err.status === 404
            ? 'E-mail não encontrado em nossa base.'
            : 'Erro ao enviar o código. Tente novamente.',
          );
          if (err.status === 404) this.emailCtrl.setErrors({ notFound: true });
        },
      });
  }

  onOtpInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const val = input.value.replace(/\D/g, '');
    input.value = val ? val[val.length - 1] : '';
    const digits = [...this.otpDigits()]; digits[index] = input.value; this.otpDigits.set(digits);
    this.otpError.set(null); this.otpHasError.set(false);
    if (input.value && index < OTP_LENGTH - 1) this.focusOtp(index + 1);
  }

  onOtpKeydown(event: KeyboardEvent, index: number): void {
    if (event.key === 'Backspace') {
      const input = event.target as HTMLInputElement;
      if (!input.value && index > 0) {
        const digits = [...this.otpDigits()]; digits[index - 1] = ''; this.otpDigits.set(digits);
        this.focusOtp(index - 1);
      }
    }
  }

  onOtpPaste(event: ClipboardEvent): void {
    event.preventDefault();
    const pasted = (event.clipboardData?.getData('text') ?? '').replace(/\D/g, '').slice(0, OTP_LENGTH);
    const digits = Array(OTP_LENGTH).fill('');
    pasted.split('').forEach((ch, i) => { digits[i] = ch; });
    this.otpDigits.set(digits);
    this.focusOtp(Math.min(pasted.length, OTP_LENGTH - 1));
  }

  onOtpFocus(event: Event): void { (event.target as HTMLInputElement).select(); }

  private focusOtp(i: number): void {
    (document.getElementById(`rotp-${i + 1}`) as HTMLInputElement | null)?.focus();
  }

  onVerifyOtp(): void {
    if (!this.canVerifyOtp()) return;
    this.otpVerifying.set(true); this.otpError.set(null);

    this.http.post<void>(`${this.apiUrl}/email-verification/password-reset/verify`, {
      email: this.emailValue, code: this.currentOtpCode(),
    }).subscribe({
      next: () => { this.otpVerifying.set(false); this.clearTimers(); this.step.set('newpass'); },
      error: (err: HttpErrorResponse) => {
        this.otpVerifying.set(false); this.otpHasError.set(true);
        this.otpDigits.set(Array(OTP_LENGTH).fill('')); this.focusOtp(0);
        this.otpError.set(
          err.status === 410 ? 'Código expirado. Solicite um novo.' :
            err.status === 400 || err.status === 401 ? 'Código incorreto. Verifique e tente novamente.' :
              'Erro ao verificar o código. Tente novamente.',
        );
      },
    });
  }

  onResend(): void {
    if (this.resendCooldown() > 0) return;
    this.otpError.set(null); this.otpDigits.set(Array(OTP_LENGTH).fill('')); this.otpHasError.set(false);
    this.startTimer();
    this.http.post<void>(`${this.apiUrl}/email-verification/password-reset/send`, { email: this.emailValue }).subscribe();
    this.resendCooldown.set(RESEND_COOLDOWN);
    this.resendIntervalId = setInterval(() => {
      const r = this.resendCooldown() - 1; this.resendCooldown.set(r);
      if (r <= 0) clearInterval(this.resendIntervalId);
    }, 1000);
  }

  onSaveNewPass(): void {
    if (this.newPassForm.invalid) return;
    this.savingPass.set(true); this.newPassError.set(null);

    this.http.post<void>(`${this.apiUrl}/email-verification/password-reset/reset`, {
      email: this.emailValue, code: this.currentOtpCode(),
      newPassword: this.newPassForm.controls.newPassword.value!,
    }).subscribe({
      next: () => { this.savingPass.set(false); this.step.set('done'); },
      error: (err: HttpErrorResponse) => {
        this.savingPass.set(false);
        if (err.status === 410) { this.newPassError.set('Sessão expirada. Inicie novamente.'); this.step.set('request'); }
        else this.newPassError.set('Erro ao redefinir a senha. Tente novamente.');
      },
    });
  }

}
