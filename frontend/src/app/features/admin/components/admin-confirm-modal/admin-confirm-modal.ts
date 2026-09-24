import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

export type ModalVariant = 'default' | 'danger';

@Component({
  selector: 'app-admin-confirm-modal',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './admin-confirm-modal.html',
  styleUrl: './admin-confirm-modal.scss',
  host: {
    role: 'dialog',
    'aria-modal': 'true',
    '[attr.aria-label]': 'ariaLabel()',
  },
})
export class AdminConfirmModal {
  readonly title       = input.required<string>();
  readonly body        = input.required<string>();
  readonly confirmLabel = input('Confirmar');
  readonly variant     = input<ModalVariant>('default');
  readonly loading     = input(false);
 
  readonly confirmed = output<void>();
  readonly cancelled = output<void>();
 
  protected readonly ariaLabel = this.title;
}
