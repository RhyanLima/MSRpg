import { Component, inject, OnInit } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RpgSystemService } from '../core/services/rpg-system.service';

@Component({
  selector: 'app-create-system',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './create-system.html',
  styleUrl: './create-system.scss',
})
export class CreateSystem {

private fb = inject(NonNullableFormBuilder);
  private rpgSystemService = inject(RpgSystemService);
  private router = inject(Router);

  error = '';
  isSubmitting = false;

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    engineVersion: ['0.1.0', Validators.required],
    contentVersion: ['1.0.0', Validators.required],
    syncPolicy: ['APPLY_TO_NEW_ONLY', Validators.required],
    // Lembrando que o Jackson no backend espera String para o settingsJson (como visto nos logs de erro anteriores)
    settingsJson: ['{ "default": "NULL" }', Validators.required]
  });

  onSubmit() {
    if (this.form.invalid) return;

    this.isSubmitting = true;
    this.error = '';

    this.rpgSystemService.createSystem(this.form.getRawValue()).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.error = 'Erro ao criar sistema: ' + (err.error?.message || err.message);
        this.isSubmitting = false;
      }
    });
  }

}
