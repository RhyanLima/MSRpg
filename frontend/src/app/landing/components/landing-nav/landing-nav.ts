import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { NavScroll } from '../../../core/services/nav-scroll';

@Component({
  selector: 'app-landing-nav',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    '[class.scrolled]': 'navScroll.isScrolled()',
  },
  imports: [],
  templateUrl: './landing-nav.html',
  styleUrl: './landing-nav.scss',
})
export class LandingNav {
  readonly navScroll = inject(NavScroll);
}
