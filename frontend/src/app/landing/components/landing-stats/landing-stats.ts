import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RevealDirective } from '../../../core/directives/reveal-directive';


interface StatItem {
  readonly value: string;
  readonly label: string;
}

@Component({
  selector: 'app-landing-stats',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RevealDirective],
  templateUrl: './landing-stats.html',
  styleUrl: './landing-stats.scss',
})
export class LandingStats {

  readonly stats: StatItem[] = [
    { value: '∞', label: 'Atributos customizáveis' },
    { value: '1d∞', label: 'Expressões de dados' },
    { value: '∞', label: 'Sistemas criados' },
    { value: 'RT', label: 'Sessões em tempo real' },
  ];

}
