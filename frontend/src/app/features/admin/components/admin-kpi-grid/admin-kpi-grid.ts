import { ChangeDetectionStrategy, Component, input } from '@angular/core';

export interface KpiItem {
  readonly label: string;
  readonly value: string | number;
  readonly delta: string;
  readonly icon: string;
  readonly accent: 'gold' | 'ember' | 'rune' | 'green';
}

@Component({
  selector: 'app-admin-kpi-grid',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './admin-kpi-grid.html',
  styleUrl: './admin-kpi-grid.scss',
})
export class AdminKpiGrid {
  readonly items = input.required<KpiItem[]>();
}
