import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ConfirmDialog } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { SystemShell } from '../../components/system-shell/system-shell';
import { RpgSystemFilter, RpgSystemSummary } from '../../models/rpg-system.model';
import { describeHttpError } from '../../services/rpg-system-errors';
import { RpgSystemService } from '../../services/rpg-system.service';

const SEARCH_MAX = 100;

/** Listagem de sistemas: busca por nome, navegação para edição e exclusão. */
@Component({
  selector: 'app-system-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, DatePipe, SystemShell, ConfirmDialog],
  templateUrl: './system-list.html',
  styleUrl: './system-list.scss',
})
export class SystemList {

  private readonly service = inject(RpgSystemService);

  protected readonly searchMax = SEARCH_MAX;

  /** Texto digitado; só vira filtro ao submeter (evita uma requisição por tecla). */
  protected readonly searchText = signal('');
  private readonly appliedFilter = signal<RpgSystemFilter>({});

  protected readonly systems = this.service.listResource(this.appliedFilter);

  protected readonly rows = computed<readonly RpgSystemSummary[]>(() =>
    this.systems.hasValue() ? this.systems.value() : []);

  protected readonly hasActiveFilter = computed(() => !!this.appliedFilter().name);

  protected readonly loadError = computed(() =>
    this.systems.error()
      ? describeHttpError(this.systems.error(), { fallback: 'Não foi possível carregar os sistemas.' })
      : null);

  protected readonly pendingDelete = signal<RpgSystemSummary | null>(null);
  protected readonly deleting = signal(false);
  protected readonly deleteError = signal<string | null>(null);

  protected onSearchInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchText.set(value.slice(0, SEARCH_MAX));
  }

  protected applySearch(event: Event): void {
    event.preventDefault();
    this.appliedFilter.set({ name: this.searchText().trim() });
  }

  protected clearSearch(): void {
    this.searchText.set('');
    this.appliedFilter.set({});
  }

  protected askDelete(system: RpgSystemSummary): void {
    this.deleteError.set(null);
    this.pendingDelete.set(system);
  }

  protected cancelDelete(): void {
    this.pendingDelete.set(null);
  }

  protected confirmDelete(): void {
    const target = this.pendingDelete();
    if (!target) {
      return;
    }
    this.deleting.set(true);
    this.service.delete(target.id).subscribe({
      next: () => {
        this.deleting.set(false);
        this.pendingDelete.set(null);
        this.systems.reload();
      },
      error: (error: unknown) => {
        this.deleting.set(false);
        this.pendingDelete.set(null);
        this.deleteError.set(describeHttpError(error, {
          404: 'Este sistema já havia sido removido.',
          409: `"${target.name}" ainda possui conteúdo (políticas, definições ou campanhas) e não pode ser removido.`,
          fallback: 'Não foi possível remover o sistema.',
        }));
        this.systems.reload();
      },
    });
  }
}
