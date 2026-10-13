import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfirmDialog } from './confirm-dialog';

@Component({
  imports: [ConfirmDialog],
  template: `
    <app-confirm-dialog heading="Excluir" confirmLabel="Excluir" variant="danger" [busy]="busy()"
      (confirmed)="confirmed = confirmed + 1" (cancelled)="cancelled = cancelled + 1">
      <p>Excluir <strong>{{ name }}</strong>?</p>
    </app-confirm-dialog>`,
})
class Host {
  readonly busy = signal(false);
  name = '<img src=x onerror=alert(1)>';
  confirmed = 0;
  cancelled = 0;
}

describe('ConfirmDialog', () => {
  let fixture: ComponentFixture<Host>;
  let host: Host;
  let element: HTMLElement;

  beforeEach(async () => {
    fixture = TestBed.createComponent(Host);
    host = fixture.componentInstance;
    element = fixture.nativeElement;
    await fixture.whenStable();
  });

  function buttons(): HTMLButtonElement[] {
    return Array.from(element.querySelectorAll('button'));
  }

  it('expõe role dialog com título associado', () => {
    const dialog = element.querySelector('[role="dialog"]')!;
    const titleId = dialog.getAttribute('aria-labelledby')!;

    expect(dialog.getAttribute('aria-modal')).toBe('true');
    expect(element.querySelector(`#${titleId}`)?.textContent).toContain('Excluir');
  });

  it('projeta o corpo como texto, sem interpretar HTML de dados do usuário', () => {
    expect(element.querySelector('img')).toBeNull();
    expect(element.querySelector('strong')?.textContent).toBe('<img src=x onerror=alert(1)>');
  });

  it('foca o botão Cancelar ao abrir', () => {
    expect(document.activeElement).toBe(buttons()[0]);
  });

  it('emite confirmed e cancelled', () => {
    buttons()[1].click();
    buttons()[0].click();

    expect(host.confirmed).toBe(1);
    expect(host.cancelled).toBe(1);
  });

  it('ESC cancela', () => {
    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));

    expect(host.cancelled).toBe(1);
  });

  it('enquanto ocupado não confirma nem cancela', async () => {
    host.busy.set(true);
    await fixture.whenStable();

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    (element.querySelector('.overlay') as HTMLElement).click();

    expect(host.cancelled).toBe(0);
    expect(buttons().every(button => button.disabled)).toBe(true);
    expect(buttons()[1].textContent).toContain('Aguarde');
  });
});
