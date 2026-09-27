import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../../admin/services/user-service';

const EMAIL_RE = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
const SPECIAL_RE = /[!@#$%^&*()\-_=+[\]{};':"\\|,.<>/?]/;
 
function passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
  const v: string = control.value ?? '';
  if (!v) return null;
  const errors: ValidationErrors = {};
  if (v.length < 8) errors['minLength'] = true;
  if (!/[A-Z]/.test(v)) errors['uppercase'] = true;
  if (!/[a-z]/.test(v)) errors['lowercase'] = true;
  if (!/\d/.test(v)) errors['number'] = true;
  if (!SPECIAL_RE.test(v)) errors['special'] = true;
  return Object.keys(errors).length ? errors : null;
}
 
function passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
  const pwd  = group.get('password')?.value;
  const conf = group.get('confirmPassword')?.value;
  return pwd && conf && pwd !== conf ? { passwordMismatch: true } : null;
}
 
function ageValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) return null;
  const bday  = new Date(control.value);
  const today = new Date();
  if (bday > today) return { futureDate: true };
  let age = today.getFullYear() - bday.getFullYear();
  const hadBirthday =
    new Date(today.getFullYear(), bday.getMonth(), bday.getDate()) <= today;
  if (!hadBirthday) age--;
  if (age < 6)   return { tooYoung: true };
  if (age > 125) return { invalidDate: true };
  return null;
}
 
type Status = 'idle' | 'loading' | 'success' | 'error';
 
interface PasswordChecks {
  len: boolean;
  upp: boolean;
  low: boolean;
  num: boolean;
  spc: boolean;
}

@Component({
  selector: 'app-register-page',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register-page.html',
  styleUrl: './register-page.scss',
})
export class RegisterPage {
  
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
 
  readonly today = new Date().toISOString().split('T')[0];
 
  readonly status = signal<Status>('idle');
  readonly errorMessage = signal<string | null>(null);
  readonly showPassword = signal(false);
  readonly showConfirm = signal(false);
  readonly createdUserId = signal<string>('');
 
  readonly form = this.fb.group(
    {
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      birthDate: ['', [Validators.required, ageValidator]],
      email: ['', [Validators.required, Validators.pattern(EMAIL_RE)]],
      password: ['', [Validators.required, passwordStrengthValidator]],
      confirmPassword: ['', Validators.required],
      terms: [false, Validators.requiredTrue],
    },
    { validators: passwordMatchValidator },
  );

  readonly canSubmit = computed(() =>
    this.form.valid && this.status() !== 'loading',
  );
 
  readonly pwdChecks = computed<PasswordChecks>(() => {
    const v = this.form.controls.password.value ?? '';
    return {
      len: v.length >= 8,
      upp: /[A-Z]/.test(v),
      low: /[a-z]/.test(v),
      num: /\d/.test(v),
      spc: SPECIAL_RE.test(v),
    };
  });
 
  readonly pwdScore = computed(() =>
    Object.values(this.pwdChecks()).filter(Boolean).length,
  );
 
  readonly pwdLevelText = computed(() => {
    const lvls = ['', 'Fraca', 'Razoável', 'Boa', 'Forte'];
    return lvls[this.pwdScore()] ?? '';
  });
 
  readonly pwdLevelColor = computed(() => {
    const cols = ['', 'var(--ember)', 'var(--gold)', '#8bc34a', 'var(--green-soft)'];
    return cols[this.pwdScore()] ?? '';
  });
 
  protected pwdBarClass(index: number): string {
    const score = this.pwdScore();
    if (index >= score) return 'pwd-bar';
    const cls = ['on-1', 'on-2', 'on-3', 'on-4'];
    return `pwd-bar ${cls[Math.min(score - 1, 3)]}`;
  }
 
  // ===== Field state helpers ===== \\
  protected isFieldValid(name: keyof typeof this.form.controls): boolean {
    const c = this.form.controls[name];
    return c.valid && c.dirty;
  }
 
  protected isFieldError(name: keyof typeof this.form.controls): boolean {
    const c = this.form.controls[name];
    return c.invalid && c.dirty;
  }
 
  protected fieldMsgClass(name: keyof typeof this.form.controls): string {
    if (this.isFieldValid(name))  return 'field-msg msg-ok';
    if (this.isFieldError(name))  return 'field-msg msg-error';
    return 'field-msg msg-hint';
  }
 
  protected fieldMsgIcon(name: keyof typeof this.form.controls): string {
    if (this.isFieldValid(name))  return '✓';
    if (this.isFieldError(name))  return '!';
    return '○';
  }
 
  readonly nameMessage = computed(() => {
    const c = this.form.controls.name;
    if (!c.dirty) return 'Como deseja ser chamado';
    if (c.errors?.['required'] || c.errors?.['minlength']) return 'Nome deve ter pelo menos 3 caracteres.';
    if (c.errors?.['maxlength']) return 'Nome deve ter no máximo 100 caracteres.';
    return 'Ótimo!';
  });
 
  readonly birthMessage = computed(() => {
    const c = this.form.controls.birthDate;
    if (!c.dirty) return 'Mínimo 6 anos';
    if (c.errors?.['required'])    return 'Data de nascimento é obrigatória.';
    if (c.errors?.['futureDate'])  return 'Data não pode ser no futuro.';
    if (c.errors?.['tooYoung'])    return 'Você precisa ter pelo menos 6 anos.';
    if (c.errors?.['invalidDate']) return 'Data inválida.';
    return 'Data válida!';
  });
 
  readonly emailMessage = computed(() => {
    const c = this.form.controls.email;
    if (!c.dirty) return 'Será usado para acessar sua conta';
    if (c.errors?.['required']) return 'E-mail é obrigatório.';
    if (c.errors?.['pattern'])  return 'Formato de e-mail inválido.';
    return 'E-mail válido!';
  });
 
  readonly passwordMessage = computed(() => {
    const c = this.form.controls.password;
    if (!c.dirty) return 'Mínimo 8 caracteres com maiúscula, número e símbolo';
    if (c.errors) return 'A senha não atende todos os requisitos.';
    return 'Senha forte!';
  });
 
  readonly confirmMessage = computed(() => {
    const c = this.form.controls.confirmPassword;
    if (!c.dirty) return 'Digite a mesma senha acima';
    if (this.form.hasError('passwordMismatch')) return 'As senhas não coincidem.';
    if (c.errors?.['required']) return 'Confirmação é obrigatória.';
    return 'Senhas coincidem!';
  });
 
  readonly confirmMsgClass = computed(() => {
    const c = this.form.controls.confirmPassword;
    if (!c.dirty) return 'field-msg msg-hint';
    if (this.form.hasError('passwordMismatch')) return 'field-msg msg-error';
    if (c.valid) return 'field-msg msg-ok';
    return 'field-msg msg-hint';
  });
 
  onSubmit(): void {

    if (!this.canSubmit()) return;
 
    this.status.set('loading');
    this.errorMessage.set(null);
 
    const { name, birthDate, email, password } = this.form.getRawValue();
 
    this.userService.register({
      name: name!.trim(),
      birthDate: birthDate!,
      email: email!.trim().toLowerCase(),
      password: password!,
    }).subscribe({
      next: (res) => {
        this.createdUserId.set(res.id);
        this.status.set('success');
      },
      error: (err: HttpErrorResponse) => {
        this.status.set('error');
        if (err.status === 409) {
          this.errorMessage.set('E-mail já cadastrado. Tente fazer login.');
          this.form.controls.email.setErrors({ conflict: true });
        } else if (err.status === 400) {
          this.errorMessage.set('Dados inválidos. Revise o formulário.');
        } else {
          this.errorMessage.set('Erro ao conectar com o servidor. Tente novamente.');
        }
      },
    });
  }
 
  goToVerify(): void {
    const email = this.form.controls.email.value ?? '';
    this.router.navigate(['/verify-email'], {
      queryParams: { email },
    });
  }

}
