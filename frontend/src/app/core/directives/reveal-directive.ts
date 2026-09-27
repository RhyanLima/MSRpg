import { afterNextRender, Directive, ElementRef, inject, input } from '@angular/core';

/**
 * Diretiva que adiciona uma classe CSS quando o elemento hospedeiro entra na área visível.
 * Usa IntersectionObserver para melhor desempenho e é compatível com SSR através de afterNextRender.
 */
@Directive({
  selector: '[appReveal]',
  host: { 
    class: 'reveal' 
  }
})
export class RevealDirective {

  private readonly element = inject(ElementRef<HTMLElement>);
  private observer: IntersectionObserver | null = null;

  /** Delay opcional em milisegundos antes da classe reveal ser apliciada */
  public revealDelay = input(0);

  public constructor() {
    afterNextRender(() => {
      this.setupObserver();
    });
  }

  private setupObserver(): void {
    this.observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const delay = this.revealDelay();
            if (delay > 0) {
              setTimeout(() => this.markVisible(), delay);
            } else {
              this.markVisible();
            }
            this.observer?.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.1, rootMargin: '0px 0px -40px 0px' }
    );

    this.observer.observe(this.element.nativeElement);
  }

  private markVisible(): void {
    this.element.nativeElement.classList.add('is-visible');
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }

}
