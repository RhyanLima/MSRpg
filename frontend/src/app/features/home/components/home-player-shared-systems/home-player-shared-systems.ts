import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
 
export type SharedSystemAccent = 'ember' | 'rune' | 'green';
export type PlayerRole = 'jogador' | 'espectador';
export type SharedSystemStatus = 'live' | 'active' | 'read-only';
 
export interface LinkedCharacter {
  readonly initial: string;
  readonly name: string;
}
 
export interface SharedSystem {
  readonly id: string;
  readonly name: string;
  readonly icon: string;
  readonly accent: SharedSystemAccent;
  readonly masterName: string;
  readonly sessionCount: number;
  readonly lastUpdated: string;
  readonly attributeCount: number;
  readonly tags: string[];
  readonly status: SharedSystemStatus;
  readonly playerRole: PlayerRole;
  readonly linkedCharacter: LinkedCharacter | null;
}
 
export interface PlayerCharacter {
  readonly id: string;
  readonly name: string;
  readonly systemName: string;
  readonly classLabel: string;
  readonly icon: string;
  readonly accent: 'ember' | 'rune';
  readonly stats: ReadonlyArray<{ readonly key: string; readonly value: number }>;
}
 
export interface PendingInvite {
  readonly id: string;
  readonly systemName: string;
  readonly masterName: string;
  readonly systemCategory: string;
  readonly playerCount: number;
  readonly icon: string;
  readonly accent: 'ember' | 'rune' | 'gold' | 'green';
}
 
const STATUS_BADGE: Record<SharedSystemStatus, string> = {
  'live':      'b-live',
  'active':    'b-live',
  'read-only': 'b-draft',
};
 
const STATUS_LABEL: Record<SharedSystemStatus, string> = {
  'live':      'Ao Vivo',
  'active':    'Ativo',
  'read-only': 'Consulta',
};
 
const ROLE_BADGE: Record<PlayerRole, string> = {
  'jogador':    'rb-pl',
  'espectador': 'rb-sp',
};
 
const ROLE_LABEL: Record<PlayerRole, string> = {
  'jogador':    'Jogador',
  'espectador': 'Espectador',
};

@Component({
  selector: 'app-home-player-shared-systems',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './home-player-shared-systems.html',
  styleUrl: './home-player-shared-systems.scss',
})
export class HomePlayerSharedSystems {

  readonly sharedSystems  = input.required<SharedSystem[]>();
  readonly characters     = input<PlayerCharacter[]>([]);
  readonly pendingInvites = input<PendingInvite[]>([]);
 
  readonly systemClick       = output<SharedSystem>();
  readonly characterClick    = output<PlayerCharacter>();
  readonly createCharClick   = output<void>();
  readonly acceptInvite      = output<PendingInvite>();
  readonly declineInvite     = output<PendingInvite>();
 
  protected cardClass(accent: SharedSystemAccent): string {
    return `sh-${accent[0]}`;
  }
 
  protected iconClass(accent: SharedSystemAccent): string {
    return `shci-${accent[0]}`;
  }
 
  protected charPortClass(accent: 'ember' | 'rune'): string {
    return `cp-${accent[0]}`;
  }
 
  protected invIconClass(accent: PendingInvite['accent']): string {
    const map: Record<string, string> = { ember: 'f', rune: 's', gold: 'g', green: 'n' };
    return `inv-icon-${map[accent] ?? 'f'}`;
  }
 
  protected statusLabel(status: SharedSystemStatus): string {
    return STATUS_LABEL[status];
  }
 
  protected statusBadgeClass(status: SharedSystemStatus): string {
    return STATUS_BADGE[status];
  }
 
  protected roleBadgeClass(role: PlayerRole): string {
    return ROLE_BADGE[role];
  }
 
  protected roleLabel(role: PlayerRole): string {
    return ROLE_LABEL[role];
  }
 
  protected isLive(status: SharedSystemStatus): boolean {
    return status === 'live';
  }
 
  protected systemMeta(s: SharedSystem): string {
    const parts = [`Mestre: ${s.masterName}`, `${s.sessionCount} sessões`, `atualizado ${s.lastUpdated}`];
    return parts.join(' · ');
  }
 
  protected inviteMeta(inv: PendingInvite): string {
    return `Mestre: ${inv.masterName} · ${inv.systemCategory} · ${inv.playerCount} jogadores`;
  }
}
