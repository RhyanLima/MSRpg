import { ChangeDetectionStrategy, Component } from '@angular/core';

interface FooterLink {
  readonly label: string;
  readonly href: string;
}

@Component({
  selector: 'app-landing-footer',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './landing-footer.html',
  styleUrl: './landing-footer.scss',
})
export class LandingFooter {

  readonly links: FooterLink[] = [
    { label: 'Docs',     href: '#' },
    { label: 'Templates', href: '#' },
    { label: 'API',      href: '#' },
    { label: 'Contato',  href: '#' },
  ];

}
