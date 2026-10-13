import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { EmberParticles } from '../../../../core/components/ember-particles/ember-particles';

export type ShellWidth = 'narrow' | 'wide';

/**
 * Moldura visual das telas de sistemas: fundo, brand e card com cabeçalho.
 * O conteúdo do card entra por projeção; ações de rodapé via [shellFooter].
 */
@Component({
  selector: 'app-system-shell',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [EmberParticles],
  templateUrl: './system-shell.html',
  styleUrl: './system-shell.scss',
})
export class SystemShell {
  readonly eyebrow = input.required<string>();
  readonly heading = input.required<string>();
  readonly subtitle = input('');
  readonly width = input<ShellWidth>('narrow');
}
