import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CalendarDay, HomeCalendar, UpcomingSession } from './components/home-calendar/home-calendar';
import { HomeLiveSession } from './components/home-live-session/home-live-session';
import { HomeSidebar } from './components/home-sidebar/home-sidebar';

export type HomeMode = 'mestre' | 'jogador';
 
const EVENT_DAYS = new Set([10, 13, 21, 27]);
const TODAY_DAY  = 13;
 
const CALENDAR_DAYS: CalendarDay[] = Array.from({ length: 31 }, (_, i) => ({
  day: i + 1,
  isToday:  i + 1 === TODAY_DAY,
  hasEvent: EVENT_DAYS.has(i + 1),
}));
 
const MESTRE_SESSIONS: UpcomingSession[] = [
  { name: 'Noite na Taverna',  when: 'Ao vivo · Fantasy Basic', accent: 'live'      },
  { name: 'A Cripta Sombria',  when: 'Sex, 13 Mar · 20h00',     accent: 'scheduled' },
  { name: 'Fronteira: Ep. 4',  when: 'Sáb, 21 Mar · 15h00',     accent: 'rune'      },
];
 
const PLAYER_SESSIONS: UpcomingSession[] = [
  { name: 'A Cripta Sombria', when: 'Sex, 13 Mar · 20h00', accent: 'scheduled' },
  { name: 'Fronteira: Ep. 4', when: 'Sáb, 21 Mar · 15h00', accent: 'rune'      },
];

@Component({
  selector: 'app-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [HomeSidebar, HomeCalendar, HomeLiveSession],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {

  private readonly router      = inject(Router);
 
  readonly mode         = signal<HomeMode>('mestre');
  readonly sidebarOpen  = signal(false);
  readonly sessionTimer = signal(5077);
 
  private timerIntervalId?: ReturnType<typeof setInterval>;
 
  readonly isMestre  = computed(() => this.mode() === 'mestre');
  readonly userName  = computed(() => 'Aventureiro');
 
  readonly sessionTimerDisplay = computed(() => {
    const s   = this.sessionTimer();
    const h   = String(Math.floor(s / 3600)).padStart(2, '0');
    const m   = String(Math.floor((s % 3600) / 60)).padStart(2, '0');
    const sec = String(s % 60).padStart(2, '0');
    return `${h}:${m}:${sec}`;
  });
 
  readonly calendarDays    = CALENDAR_DAYS;
  readonly mestreSessions  = MESTRE_SESSIONS;
  readonly playerSessions  = PLAYER_SESSIONS;
 
  ngOnInit(): void {
    this.timerIntervalId = setInterval(
      () => this.sessionTimer.update(s => s + 1),
      1000,
    );
  }
 
  ngOnDestroy(): void {
    if (this.timerIntervalId) clearInterval(this.timerIntervalId);
  }
 
  setMode(mode: 'mestre' | 'jogador'): void {
    this.mode.set(mode);
    this.closeSidebar();
  }
 
  toggleSidebar(): void { this.sidebarOpen.update(v => !v); }
  closeSidebar():  void { this.sidebarOpen.set(false); }
 
  logout(): void {
    this.router.navigate(['/auth/login']);
  }

}
