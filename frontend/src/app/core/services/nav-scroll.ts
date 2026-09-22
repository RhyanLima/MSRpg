import { afterNextRender, Injectable, OnDestroy, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class NavScroll implements OnDestroy {

  private readonly SCROLL_THRESHOLD = 60;
  private readonly boundHandler = this.onScroll.bind(this);

  readonly isScrolled = signal(false);

  constructor() {
    afterNextRender(() => {
      window.addEventListener('scroll', this.boundHandler, { passive: true });
      this.onScroll();
    });
  }

  private onScroll(): void {
    this.isScrolled.set(window.scrollY > this.SCROLL_THRESHOLD);
  }

  ngOnDestroy(): void {
    window.removeEventListener('scroll', this.boundHandler);
  }

}
