import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-home-live-session',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './home-live-session.html',
  styleUrl: './home-live-session.scss',
})
export class HomeLiveSession {
  readonly sessionName   = input('');
  readonly systemName    = input('');
  readonly playerCount   = input(0);
  readonly timerDisplay  = input('00:00:00');
}
