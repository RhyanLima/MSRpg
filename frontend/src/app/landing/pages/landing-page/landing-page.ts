import { ChangeDetectionStrategy, Component } from '@angular/core';
import { LandingNav } from '../../components/landing-nav/landing-nav';
import { LandingHero } from '../../components/landing-hero/landing-hero';
import { LandingHow } from '../../components/landing-how/landing-how';
import { LandingFeatures } from '../../components/landing-features/landing-features';
import { LandingCta } from '../../components/landing-cta/landing-cta';
import { LandingFooter } from '../../components/landing-footer/landing-footer';
import { LandingStats } from '../../components/landing-stats/landing-stats';
import { LandingRulesEngine } from '../../components/landing-rules-engine/landing-rules-engine';
import { LandingRoles } from '../../components/landing-roles/landing-roles';

@Component({
  selector: 'app-landing-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    LandingNav,
    LandingHero,
    LandingStats,
    LandingHow,
    LandingFeatures,
    LandingRulesEngine,
    LandingRoles,
    LandingCta,
    LandingFooter
  ],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage {

}
