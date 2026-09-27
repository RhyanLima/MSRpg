import { ChangeDetectionStrategy, Component, input } from '@angular/core';

export interface CalendarDay {
  readonly day: number;
  readonly isToday: boolean;
  readonly hasEvent: boolean;
}
 
export interface UpcomingSession {
  readonly name: string;
  readonly when: string;
  readonly accent: 'live' | 'scheduled' | 'rune';
}

@Component({
  selector: 'app-home-calendar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './home-calendar.html',
  styleUrl: './home-calendar.scss',
})
export class HomeCalendar {
  readonly monthLabel       = input('Março 2026');
  readonly days             = input.required<CalendarDay[]>();
  readonly upcomingSessions = input<UpcomingSession[]>([]);
}
