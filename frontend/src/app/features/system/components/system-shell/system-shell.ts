import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { EmberParticles } from '../../../shared/components/ember-particles/ember-particles';

export type ShellWidth = 'narrow' | 'wide';

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
