import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { SystemForm } from '../../components/system-form/system-form';
import { SystemShell } from '../../components/system-shell/system-shell';
import { RpgSystemDraft, toRequest } from '../../models/rpg-system.model';
import { describeHttpError } from '../../services/rpg-system-errors';
import { RpgSystemService } from '../../services/rpg-system.service';

@Component({
  selector: 'app-create-system',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, SystemShell, SystemForm],
  templateUrl: './create-system.html',
  styleUrl: './create-system.scss',
})
export class CreateSystem {

  private readonly service = inject(RpgSystemService);
  private readonly router = inject(Router);

  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected save(draft: RpgSystemDraft): void {
    this.saving.set(true);
    this.errorMessage.set(null);

    this.service.create(toRequest(draft)).subscribe({
      next: () => void this.router.navigate(['/systems']),
      error: (error: unknown) => {
        this.saving.set(false);
        this.errorMessage.set(describeHttpError(error, {
          409: 'Já existe um sistema com esse nome.',
          fallback: 'Não foi possível criar o sistema. Tente novamente.',
        }));
      },
    });
  }
}
