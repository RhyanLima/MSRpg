import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
 
export type SystemStatus = 'live' | 'active' | 'draft';
export type SystemAccent = 'ember' | 'rune' | 'green' | 'gold';
 
export interface RpgSystemRow {
  readonly id: string;
  readonly name: string;
  readonly icon: string;
  readonly accent: SystemAccent;
  readonly status: SystemStatus;
  readonly attributeCount: number;
  readonly playerCount: number;
  readonly lastEdited: string;
  readonly shared: boolean;
}
 
export type CampaignStatus = 'active' | 'paused' | 'completed';
export type CampaignAccent = 'ember' | 'rune' | 'gold';
 
export interface CampaignRow {
  readonly id: string;
  readonly name: string;
  readonly icon: string;
  readonly accent: CampaignAccent;
  readonly systemName: string;
  readonly playerCount: number;
  readonly sessionCount: number;
  readonly progressPercent: number;
  readonly status: CampaignStatus;
}

const STATUS_LABEL: Record<SystemStatus, string> = {
  live:   'Ao Vivo',
  active: 'Ativo',
  draft:  'Rascunho',
};
 
const STATUS_BADGE: Record<SystemStatus, string> = {
  live:   'b-live',
  active: 'b-live',
  draft:  'b-draft',
};

@Component({
  selector: 'app-home-systems-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './home-systems-list.html',
  styleUrl: './home-systems-list.scss',
})
export class HomeSystemsList {

  readonly systems   = input.required<RpgSystemRow[]>();
  readonly campaigns = input<CampaignRow[]>([]);
 
  readonly showCreateSystem   = input(true);
  readonly showCreateCampaign = input(true);
 
  readonly systemClick         = output<RpgSystemRow>();
  readonly campaignClick       = output<CampaignRow>();
  readonly createSystemClick   = output<void>();
  readonly createCampaignClick = output<void>();
 
  protected statusLabel(status: SystemStatus): string {
    return STATUS_LABEL[status];
  }
 
  protected statusBadgeClass(status: SystemStatus): string {
    return STATUS_BADGE[status];
  }
 
  protected isLive(status: SystemStatus): boolean {
    return status === 'live';
  }
 
  protected systemIconClass(accent: SystemAccent): string {
    return `si-${accent[0]}`;
  }
 
  protected campaignIconClass(accent: CampaignAccent): string {
    return `ct-${accent[0]}`;
  }
 
  protected systemMetaLabel(s: RpgSystemRow): string {
    const parts: string[] = [`${s.attributeCount} atributos`];
    if (s.playerCount > 0) parts.push(`${s.playerCount} jogadores`);
    parts.push(`editado ${s.lastEdited}`);
    return parts.join(' · ');
  }
 
  protected campaignMetaLabel(c: CampaignRow): string {
    return `${c.systemName} · ${c.playerCount} jogadores · ${c.sessionCount} sessões`;
  }
}
