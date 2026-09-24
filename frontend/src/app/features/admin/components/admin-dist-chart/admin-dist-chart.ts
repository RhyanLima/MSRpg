import { ChangeDetectionStrategy, Component, input } from '@angular/core';

export interface DistItem {
  readonly label: string;
  readonly count: number;
  readonly percent: number;
  readonly type: 'common' | 'admin' | 'dev';
}

@Component({
  selector: 'app-admin-dist-chart',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './admin-dist-chart.html',
  styleUrl: './admin-dist-chart.scss',
})
export class AdminDistChart {
  readonly items = input.required<DistItem[]>();
}
