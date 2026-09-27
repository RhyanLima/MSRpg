import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, input, OnDestroy, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { EmailVerificationService } from '../../services/email-verification-service';

export type OtpPagePurpose = 'email-verification' | '2fa';
type OtpStatus = 'idle' | 'loading' | 'success' | 'error' | 'expired';
 
const OTP_LENGTH = 6;
const TIMER_SECONDS = 15 * 60;
const RESEND_COOLDOWN_SECONDS = 60;

@Component({
  selector: 'app-otp-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './otp-page.html',
  styleUrl: './otp-page.scss',
})
export class OtpPage implements OnInit, OnDestroy {

  readonly purpose = input.required<OtpPagePurpose>();
  readonly email = input.required<string>();
 
  private readonly verificationService = inject(EmailVerificationService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
 
  readonly otpStatus = signal<OtpStatus>('idle');
  readonly errorMessage = signal<string | null>(null);
  readonly otpDigits = signal<string[]>(Array(OTP_LENGTH).fill(''));
  readonly secondsLeft = signal(TIMER_SECONDS);
  readonly resendCooldown = signal(0);
 
  readonly otpIndices = [0, 1, 2, 3, 4, 5];
 
  private timerIntervalId?: ReturnType<typeof setInterval>;
  private resendIntervalId?: ReturnType<typeof setInterval>;
 
  readonly cardLabel = computed(() =>
    this.purpose() === '2fa' ? 'Autenticação em dois fatores' : 'Verificação de e-mail',
  );
 
  readonly eyebrow = computed(() =>
    this.purpose() === '2fa' ? 'Autenticação de Dois Fatores' : 'Verificação de E-mail',
  );
 
  readonly subtitle = computed(() =>
    this.purpose() === '2fa' ? 'Confirme sua identidade' : 'Ative sua conta',
  );
 
  readonly verifyLabel = computed(() =>
    this.purpose() === '2fa' ? 'Confirmar Acesso' : 'Verificar Identidade',
  );
 
  readonly successTitle = computed(() =>
    this.purpose() === '2fa' ? 'Identidade Confirmada' : 'E-mail Verificado',
  );
 
  readonly successDesc = computed(() =>
    this.purpose() === '2fa'
      ? 'Sua identidade foi verificada com sucesso.\nBem-vindo de volta ao MSRpg.'
      : 'Sua conta foi ativada com sucesso.\nVocê já pode fazer login.',
  );
 
  readonly maskedEmail = computed(() => {
    const e = this.email();
    if (!e) return '';
    const [local, domain] = e.split('@');
    if (!domain) return e;
    return `${local[0]}***@${domain}`;
  });
 
  readonly timerDisplay = computed(() => {
    const s = this.secondsLeft();
    const m = String(Math.floor(s / 60)).padStart(2, '0');
    const sec = String(s % 60).padStart(2, '0');
    return `${m}:${sec}`;
  });
 
  readonly canVerify = computed(() => {
    const filled = this.otpDigits().every(d => d !== '');
    return filled && this.otpStatus() !== 'loading' && this.otpStatus() !== 'expired';
  });
 
  readonly currentCode = computed(() => this.otpDigits().join(''));
 
  ngOnInit(): void {
    this.startTimer();
  }
 
  ngOnDestroy(): void {
    this.clearTimers();
  }
 
  private startTimer(): void {
    this.clearTimers();
    this.secondsLeft.set(TIMER_SECONDS);
    this.timerIntervalId = setInterval(() => {
      const remaining = this.secondsLeft() - 1;
      this.secondsLeft.set(remaining);
      if (remaining <= 0) {
        clearInterval(this.timerIntervalId);
        this.otpStatus.set('expired');
        this.errorMessage.set('Código expirado. Solicite um novo código.');
      }
    }, 1000);
  }
 
  private clearTimers(): void {
    if (this.timerIntervalId)  clearInterval(this.timerIntervalId);
    if (this.resendIntervalId) clearInterval(this.resendIntervalId);
  }
 
  protected onInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const val = input.value.replace(/\D/g, '');
    input.value = val ? val[val.length - 1] : '';
 
    const newDigits = [...this.otpDigits()];
    newDigits[index] = input.value;
    this.otpDigits.set(newDigits);
 
    this.errorMessage.set(null);
    if (this.otpStatus() === 'error') this.otpStatus.set('idle');
 
    if (input.value && index < OTP_LENGTH - 1) {
      this.focusInput(index + 1);
    }
  }
 
  protected onKeydown(event: KeyboardEvent, index: number): void {
    if (event.key === 'Backspace') {
      const input = event.target as HTMLInputElement;
      if (!input.value && index > 0) {
        const newDigits = [...this.otpDigits()];
        newDigits[index - 1] = '';
        this.otpDigits.set(newDigits);
        this.focusInput(index - 1);
      }
    }
  }
 
  protected onPaste(event: ClipboardEvent): void {
    event.preventDefault();
    const pasted = (event.clipboardData?.getData('text') ?? '').replace(/\D/g, '').slice(0, OTP_LENGTH);
    const newDigits = Array(OTP_LENGTH).fill('');
    pasted.split('').forEach((ch, i) => { newDigits[i] = ch; });
    this.otpDigits.set(newDigits);
    this.focusInput(Math.min(pasted.length, OTP_LENGTH - 1));
  }
 
  protected onFocus(event: Event): void {
    (event.target as HTMLInputElement).select();
  }
 
  private focusInput(index: number): void {
    const el = document.getElementById(`otp-${index + 1}`) as HTMLInputElement | null;
    el?.focus();
  }
 
  protected onVerify(): void {
    if (!this.canVerify()) return;
 
    this.otpStatus.set('loading');
    this.errorMessage.set(null);
 
    const verify$ = this.purpose() === '2fa'
      ? this.verificationService.verify2fa(this.email(), this.currentCode())
      : this.verificationService.verifyEmail(this.email(), this.currentCode());
 
    verify$.subscribe({
      next: () => {
        this.clearTimers();
        this.otpStatus.set('success');
      },
      error: (err: HttpErrorResponse) => {
        this.otpStatus.set('error');
        this.otpDigits.set(Array(OTP_LENGTH).fill(''));
        this.focusInput(0);
 
        if (err.status === 400 || err.status === 401) {
          this.errorMessage.set('Código inválido. Verifique e tente novamente.');
        } else if (err.status === 410) {
          this.otpStatus.set('expired');
          this.errorMessage.set('Código expirado. Solicite um novo código.');
        } else {
          this.errorMessage.set('Erro ao verificar. Tente novamente.');
        }
      },
    });
  }
 
  protected onResend(): void {
    if (this.resendCooldown() > 0) return;
 
    this.errorMessage.set(null);
    this.otpDigits.set(Array(OTP_LENGTH).fill(''));
    this.otpStatus.set('idle');
    this.startTimer();
 
    const send$ = this.purpose() === '2fa'
      ? this.verificationService.send2fa(this.email())
      : this.verificationService.sendEmailVerification(this.email());
 
    send$.subscribe();
 
    this.resendCooldown.set(RESEND_COOLDOWN_SECONDS);
    this.resendIntervalId = setInterval(() => {
      const remaining = this.resendCooldown() - 1;
      this.resendCooldown.set(remaining);
      if (remaining <= 0) clearInterval(this.resendIntervalId);
    }, 1000);
  }
 
  protected onContinue(): void {
    if (this.purpose() === 'email-verification') {
      this.router.navigate(['/login']);
    } else {
      const accountType = this.authService.accountType();
      if (accountType === 'ADMIN' || accountType === 'DEV') {
        this.router.navigate(['/admin']);
      } else {
        this.router.navigate(['/']);
      }
    }
  }

}
