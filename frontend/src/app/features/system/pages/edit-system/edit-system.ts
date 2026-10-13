import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { map } from 'rxjs';
import { SystemForm } from '../../components/system-form/system-form';
import { SystemShell } from '../../components/system-shell/system-shell';
import { RpgSystemDraft, draftOf, toRequest } from '../../models/rpg-system.model';
import { describeHttpError } from '../../services/rpg-system-errors';
import { RpgSystemService } from '../../services/rpg-system.service';

const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

/** Visualização e edição de um sistema. engineVersion é exibida, nunca editável. */
@Component({
  selector: 'app-edit-system',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, DatePipe, SystemShell, SystemForm],
  templateUrl: './edit-system.html',
  styleUrl: './edit-system.scss',
})
export class EditSystem {

  private readonly service = inject(RpgSystemService);
  private readonly router = inject(Router);

  private readonly routeId = toSignal(
    inject(ActivatedRoute).paramMap.pipe(map(params => params.get('id') ?? '')),
    { initialValue: '' },
  );

  /** IDs fora do formato UUID nem chegam a gerar requisição. */
  protected readonly invalidId = computed(() => !UUID_PATTERN.test(this.routeId()));
  private readonly systemId = computed(() => (this.invalidId() ? undefined : this.routeId()));

  protected readonly system = this.service.detailResource(this.systemId);

  protected readonly draft = computed<RpgSystemDraft | null>(() =>
    this.system.hasValue() ? draftOf(this.system.value()) : null);

  protected readonly loadError = computed(() =>
    this.system.error()
      ? describeHttpError(this.system.error(), {
          404: 'Sistema não encontrado. Ele pode ter sido removido.',
          fallback: 'Não foi possível carregar o sistema.',
        })
      : null);

  protected readonly saving = signal(false);
  protected readonly saveError = signal<string | null>(null);

  protected save(draft: RpgSystemDraft): void {
    const id = this.systemId();
    if (!id) {
      return;
    }
    this.saving.set(true);
    this.saveError.set(null);

    this.service.update(id, toRequest(draft)).subscribe({
      next: () => void this.router.navigate(['/systems']),
      error: (error: unknown) => {
        this.saving.set(false);
        this.saveError.set(describeHttpError(error, {
          404: 'Sistema não encontrado. Ele pode ter sido removido.',
          409: 'Já existe outro sistema com esse nome.',
          fallback: 'Não foi possível salvar as alterações.',
        }));
      },
    });
  }
}
