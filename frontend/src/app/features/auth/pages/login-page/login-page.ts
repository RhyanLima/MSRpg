import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { EmailVerificationService } from '../../services/email-verification-service';
import { EmberParticles } from '../../../../core/components/ember-particles/ember-particles';

type LoginStatus = 'idle' | 'loading' | 'error';

@Component({
  selector: 'app-login-page',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, RouterLink, EmberParticles],
  templateUrl: './login-page.html',
  styleUrl: './login-page.scss',
})
export class LoginPage {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly verificationService = inject(EmailVerificationService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
 
  readonly showPassword = signal(false);
  readonly status = signal<LoginStatus>('idle');
  readonly errorMessage = signal('');
  readonly verifiedBanner = signal(false);
 
  readonly loginForm = this.fb.nonNullable.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });
 
  readonly emailError = computed(() => {
    const ctrl = this.loginForm.controls.email;
    if (ctrl.hasError('required')) return 'E-mail é obrigatório';
    if (ctrl.hasError('email'))    return 'Formato de e-mail inválido';
    return '';
  });
 
  ngOnInit(): void {
    const verified = this.route.snapshot.queryParamMap.get('verified');
    if (verified === 'true') this.verifiedBanner.set(true);
  }
 
  protected isFieldInvalid(field: 'email' | 'password'): boolean {
    const ctrl = this.loginForm.controls[field];
    return ctrl.invalid && ctrl.touched;
  }
 
  async onSubmit(): Promise<void> {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
 
    this.status.set('loading');
    this.errorMessage.set('');
 
    try {
      const { email, password } = this.loginForm.getRawValue();
      const response = await this.authService.login({ email, password });
 
      const isAdmin = response.type === 'ADMIN' || response.type === 'DEV';
 
      if (isAdmin) {
        /* Administradores: enviar OTP 2FA antes de liberar acesso */
        await this.verificationService.send2fa(email).toPromise();
        this.router.navigate(['/verify-2fa'], { queryParams: { email } });
      } else {
        /* Usuário comum: vai direto para home */
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        this.router.navigateByUrl(returnUrl ?? '/home');
      }
    } catch (err: unknown) {
      this.status.set('error');
      this.errorMessage.set(this.extractErrorMessage(err));
    }
  }
 
  private extractErrorMessage(err: unknown): string {
    if (err && typeof err === 'object' && 'status' in err) {
      const s = (err as { status: number }).status;
      if (s === 401) return 'Credenciais inválidas. Verifique seu e-mail e senha.';
      if (s === 403) return 'Acesso negado.';
      if (s === 429) return 'Muitas tentativas. Aguarde alguns minutos.';
      if (s === 0)   return 'Servidor indisponível. Tente novamente.';
    }
    return 'Erro ao autenticar. Tente novamente.';
  }

}
