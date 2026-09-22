import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RevealDirective } from '../../../core/directives/reveal-directive';

interface Step {
  readonly number: string;
  readonly icon: string;
  readonly title: string;
  readonly description: string;
  readonly delay: number;
}

@Component({
  selector: 'app-landing-how',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RevealDirective],
  templateUrl: './landing-how.html',
  styleUrl: './landing-how.scss',
})
export class LandingHow {

  readonly steps: Step[] = [
    {
      number: '01',
      icon: '🏛️',
      title: 'Crie Seu Sistema',
      description:
        'Defina atributos, itens, habilidades e regras usando nosso Rules Engine com DSL visual. Configure tudo do zero ou importe um template pronto.',
      delay: 0,
    },
    {
      number: '02',
      icon: '🧙‍♂️',
      title: 'Monte Sua Mesa',
      description:
        'Crie personagens, inimigos e NPCs. Posicione entidades no tabuleiro, defina a ordem de turno e configure as permissões dos jogadores.',
      delay: 150,
    },
    {
      number: '03',
      icon: '⚔️',
      title: 'Jogue em Tempo Real',
      description:
        'O motor resolve automaticamente dados, efeitos e regras encadeadas. Todos os resultados ficam no log de auditoria da sessão.',
      delay: 300,
    },
  ];

}
