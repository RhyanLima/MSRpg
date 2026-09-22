import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RevealDirective } from '../../../core/directives/reveal-directive';

interface RoleCard {
  readonly id: string;
  readonly badge: string;
  readonly title: string;
  readonly description: string;
  readonly perks: string[];
  readonly delay: number;
}

@Component({
  selector: 'app-landing-roles',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RevealDirective],
  templateUrl: './landing-roles.html',
  styleUrl: './landing-roles.scss',
})
export class LandingRoles {

  readonly roles: RoleCard[] = [
    {
      id: 'master',
      badge: 'Mestre',
      title: 'Arquiteto do Mundo',
      description:
        'Crie e gerencie o sistema inteiro. Controle o que cada jogador pode ver, fazer ou modificar dentro da sua mesa.',
      perks: [
        'Criação e edição completa do sistema',
        'Gerenciamento de sessões e entidades',
        'Configuração granular de permissões',
        'Rolagens secretas e ações ocultas',
        'Export / Import de sistemas completos',
        'Dashboard de auditoria e logs',
      ],
      delay: 0,
    },
    {
      id: 'player',
      badge: 'Jogador',
      title: 'Herói da Narrativa',
      description:
        'Participe da mesa com as permissões definidas pelo mestre. Gerencie seu personagem e interaja com o mundo criado.',
      perks: [
        'Ficha de personagem interativa',
        'Ações e combate em tempo real',
        'Acesso às regras do sistema ativo',
        'Inventário e equipamentos',
        'Visibilidade controlada pelo mestre',
        'Log pessoal de ações',
      ],
      delay: 150,
    },
  ];

}
