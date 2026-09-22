import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RevealDirective } from '../../../core/directives/reveal-directive';

interface Feature {
  readonly icon: string;
  readonly title: string;
  readonly description: string;
  readonly accentIndex: number; // 0=ember, 1=gold, 2=rune (cycles)
}

@Component({
  selector: 'app-landing-features',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RevealDirective],
  templateUrl: './landing-features.html',
  styleUrl: './landing-features.scss',
})
export class LandingFeatures {

  readonly features: Feature[] = [
    {
      icon: '🎲',
      title: 'Rules Engine',
      description:
        'Defina regras como JSON estruturado. Triggers encadeados, expressões de dados e efeitos compostos.',
      accentIndex: 0,
    },
    {
      icon: '📊',
      title: 'Atributos Livres',
      description:
        'Crie qualquer atributo com fórmulas customizadas. Vida, mana, sanidade — o que sua lore pedir.',
      accentIndex: 1,
    },
    {
      icon: '🌐',
      title: 'Sessões ao Vivo',
      description:
        'WebSocket nativo. Estado sincronizado entre mestre e jogadores em tempo real via Redis.',
      accentIndex: 2,
    },
    {
      icon: '📜',
      title: 'Log de Auditoria',
      description:
        'Cada ação, rolagem e efeito registrado. Replay de estados e transparência total nas rolagens.',
      accentIndex: 0,
    },
    {
      icon: '🔒',
      title: 'Permissões Granulares',
      description:
        'Controle por system e por sessão. Defina exatamente o que cada jogador pode ver ou editar.',
      accentIndex: 1,
    },
    {
      icon: '📦',
      title: 'Import / Export',
      description:
        'Exporte seu sistema completo como JSON. Compartilhe e importe em qualquer ambiente.',
      accentIndex: 2,
    },
  ];

  public accentClass(index: number): string {
    const classes = ['ember', 'gold', 'rune'];
    return classes[index % classes.length];
  }

}
