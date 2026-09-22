import { Component } from '@angular/core';

@Component({
  selector: 'app-landing-hero',
  imports: [],
  templateUrl: './landing-hero.html',
  styleUrl: './landing-hero.scss',
})
export class LandingHero {

  readonly particles = [
    { id: 1, left: '8%',  dur: '14s', delay: '0s',    drift: '30px'  },
    { id: 2, left: '17%', dur: '11s', delay: '2s',    drift: '-20px' },
    { id: 3, left: '28%', dur: '16s', delay: '0.5s',  drift: '40px'  },
    { id: 4, left: '42%', dur: '12s', delay: '4s',    drift: '-30px' },
    { id: 5, left: '55%', dur: '18s', delay: '1.5s',  drift: '25px'  },
    { id: 6, left: '68%', dur: '13s', delay: '3s',    drift: '-40px' },
    { id: 7, left: '80%', dur: '15s', delay: '0.8s',  drift: '20px'  },
    { id: 8, left: '91%', dur: '10s', delay: '5s',    drift: '-25px' },
  ];

}
