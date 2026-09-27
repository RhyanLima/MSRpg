import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RpgSystemService } from '../../services/rpg-system.service';
import { HttpErrorResponse } from '@angular/common/http';
import { EmberParticles } from '../../../../core/components/ember-particles/ember-particles';

type Status = 'idle' | 'loading' | 'error';

const SYNC_POLICIES = [
  ['APPLY_TO_NEW_ONLY', 'Aplicar somente a novos elementos'],
  ['APPLY_TO_CAMPAIGN', 'Aplicar à campanha atual'],
  ['APPLY_NEXT_CAMPAIGN', 'Aplicar na próxima campanha'],
] as const;

const COMMIT_STRATEGIES = [
  ['BATCHED', 'Em lote'],
  ['IMMEDIATE', 'Imediato'],
] as const;

const MISSING_COMPONENT_POLICIES = [
  ['WARN_AND_SKIP_STEP', 'Avisar e pular etapa'],
  ['FAIL_EVENT', 'Falhar o evento'],
  ['IGNORE_SILENTLY', 'Ignorar silenciosamente'],
] as const;

const CYCLE_LIMIT_BEHAVIORS = [
  ['ABORT_AND_WARN', 'Abortar e avisar'],
  ['ABORT_AND_FAIL', 'Abortar e falhar'],
] as const;

const SNAPSHOT_FREQUENCIES = [
  ['DISABLED', 'Desabilitado'],
  ['EVERY_TURN_END', 'A cada fim de turno'],
  ['EVERY_COMBAT_END', 'A cada fim de combate'],
  ['EVERY_SESSION_END', 'A cada fim de sessão'],
] as const;

const LOG_LEVELS = [
  ['DEBUG', 'Debug'],
  ['INFO', 'Info'],
  ['WARN', 'Warn'],
  ['ERROR', 'Error'],
] as const;

const CONFLICT_STRATEGIES = [
  ['ASK_USER', 'Perguntar ao usuário'],
  ['SKIP', 'Ignorar'],
  ['OVERWRITE', 'Sobrescrever'],
  ['CREATE_COPY', 'Criar cópia'],
] as const;

@Component({
  selector: 'app-create-system',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, RouterLink, EmberParticles],
  templateUrl: './create-system.html',
  styleUrl: './create-system.scss',
})
export class CreateSystem {

  private fb = inject(NonNullableFormBuilder);
  private rpgSystemService = inject(RpgSystemService);
  private router = inject(Router);

  protected readonly syncPolicies = SYNC_POLICIES;
  protected readonly commitStrategies = COMMIT_STRATEGIES;
  protected readonly missingComponentPolicies = MISSING_COMPONENT_POLICIES;
  protected readonly cycleLimitBehaviors = CYCLE_LIMIT_BEHAVIORS;
  protected readonly snapshotFrequencies = SNAPSHOT_FREQUENCIES;
  protected readonly logLevels = LOG_LEVELS;
  protected readonly conflictStrategies = CONFLICT_STRATEGIES;

  readonly status = signal<Status>('idle');
  readonly errorMessage = signal<string | null>(null);
  readonly advancedSettings = signal(false);

  readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    description: ['', Validators.maxLength(500)],
    engineVersion: ['0.1.0', Validators.required],
    contentVersion: ['1.0.0', Validators.required],
    defaultResolutionPolicyId: ['3d228a06-6309-43fb-82f7-5c13a83e7edf', Validators.required],
    syncPolicy: ['APPLY_TO_NEW_ONLY' as string, Validators.required],
    settings: this.fb.group({
      runtime: this.fb.group({
        modifierCommitStrategy: ['BATCHED' as string],
        missingComponentPolicy: ['WARN_AND_SKIP_STEP' as string],
        cycleDetection: this.fb.group({
          enabled: [true],
          maxDepth: [20, [Validators.required, Validators.min(1)]],
          onLimitReached: ['ABORT_AND_WARN' as string],
        }),
      }),
      snapshots: this.fb.group({
        manualSnapshotsEnabled: [true],
        automaticSnapshotsEnabled: [true],
        automaticFrequency: ['EVERY_SESSION_END' as string],
        maxSnapshotsToKeep: [10, [Validators.required, Validators.min(0)]],
      }),
      logs: this.fb.group({
        minimumSessionLogLevel: ['INFO' as string],
        maxSessionLogsToKeep: [1000, [Validators.required, Validators.min(0)]],
      }),
      importExport: this.fb.group({
        defaultConflictResolutionStrategy: ['ASK_USER' as string],
        includeDependenciesByDefault: [true],
      }),
    }),
  });

  readonly canSubmit = computed(() => this.form.valid && this.status() !== 'loading');

  protected toggleAdvancedSettings(checked: boolean): void {
    this.advancedSettings.set(checked);
  }

  protected isFieldError(name: 'name' | 'engineVersion' | 'contentVersion' | 'defaultResolutionPolicyId'): boolean {
    const control = this.form.controls[name];
    return control.invalid && control.dirty;
  }

  onSubmit() {
   if (!this.canSubmit()) return;

    this.status.set('loading');
    this.errorMessage.set(null);

    const { name, description, engineVersion, contentVersion, defaultResolutionPolicyId, syncPolicy } =
      this.form.getRawValue();

    const settingsJson = this.advancedSettings()
      ? JSON.stringify(this.form.controls.settings.getRawValue())
      : '{}';

    this.rpgSystemService
      .createSystem({
        name: name!.trim(),
        description: (description ?? '').trim(),
        engineVersion: engineVersion!,
        contentVersion: contentVersion!,
        defaultResolutionPolicyId: defaultResolutionPolicyId!,
        syncPolicy: syncPolicy!,
        settingsJson,
      })
      .subscribe({
        next: () => this.router.navigate(['/']),
        error: (err: HttpErrorResponse) => {
          this.status.set('error');
          this.errorMessage.set(this.extractErrorMessage(err));
        },
      });
  }

  private extractErrorMessage(err: HttpErrorResponse): string {
    if (err.status === 409) return 'Já existe um sistema cadastrado com esse nome.';
    if (err.status === 400) return 'Dados inválidos. Revise o formulário.';
    if (err.status === 0) return 'Servidor indisponível. Tente novamente.';
    return 'Erro ao criar sistema. Tente novamente.';
  }

}
