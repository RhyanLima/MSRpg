import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  afterNextRender,
  input,
  output,
  viewChild,
} from '@angular/core';

export type ConfirmDialogVariant = 'default' | 'danger';

let nextId = 0;

/**
 * Diálogo modal de confirmação.
 *
 * O corpo entra por projeção de conteúdo (<ng-content>), nunca por HTML em
 * string: dados do usuário (ex.: nome do sistema) são sempre interpolados
 * pelo Angular e não podem injetar markup.
 *
 * Acessibilidade: role="dialog", aria-modal, título associado, foco inicial no
 * botão "Cancelar" (ação segura) e ESC para fechar.
 */
@Component({
  selector: 'app-confirm-dialog',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.scss',
  host: {
    '(document:keydown.escape)': 'cancel()',
  },
})
export class ConfirmDialog {

  readonly heading = input.required<string>();
  readonly confirmLabel = input('Confirmar');
  readonly variant = input<ConfirmDialogVariant>('default');
  readonly busy = input(false);

  readonly confirmed = output<void>();
  readonly cancelled = output<void>();

  protected readonly titleId = `confirm-dialog-title-${nextId++}`;

  private readonly cancelButton = viewChild.required<ElementRef<HTMLButtonElement>>('cancelButton');

  constructor() {
    afterNextRender(() => this.cancelButton().nativeElement.focus());
  }

  protected confirm(): void {
    if (!this.busy()) {
      this.confirmed.emit();
    }
  }

  protected cancel(): void {
    if (!this.busy()) {
      this.cancelled.emit();
    }
  }
}
