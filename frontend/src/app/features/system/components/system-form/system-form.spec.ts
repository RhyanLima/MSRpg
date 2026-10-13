import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SystemForm } from './system-form';
import { EMPTY_DRAFT, RpgSystemDraft } from '../../models/rpg-system.model';
import { typeInto } from '../../testing/system-testing';

@Component({
  imports: [SystemForm],
  template: `<app-system-form [initial]="initial()" [busy]="busy()" submitLabel="Salvar" (saved)="saved.push($event)" />`,
})
class Host {
  readonly initial = signal<RpgSystemDraft>(EMPTY_DRAFT);
  readonly busy = signal(false);
  readonly saved: RpgSystemDraft[] = [];
}

describe('SystemForm', () => {
  let fixture: ComponentFixture<Host>;
  let host: Host;
  let element: HTMLElement;

  beforeEach(async () => {
    fixture = TestBed.createComponent(Host);
    host = fixture.componentInstance;
    element = fixture.nativeElement;
    await fixture.whenStable();
  });

  const input = (id: string) => element.querySelector<HTMLInputElement>(`#${id}`)!;
  const submitButton = () => element.querySelector<HTMLButtonElement>('button[type="submit"]')!;

  it('começa com os defaults do banco e o botão desabilitado (nome obrigatório)', () => {
    expect(input('system-content-version').value).toBe('1.0.0');
    expect(element.querySelector<HTMLSelectElement>('#system-sync-policy')!.value).toBe('APPLY_TO_NEW_ONLY');
    expect(submitButton().disabled).toBe(true);
  });

  it('mostra erro de tamanho mínimo após tocar o campo', async () => {
    typeInto(input('system-name'), 'ab');
    await fixture.whenStable();

    expect(element.querySelector('#system-name-msg')?.textContent).toContain('pelo menos 3');
    expect(submitButton().disabled).toBe(true);
  });

  it('valida o formato da versão de conteúdo', async () => {
    typeInto(input('system-name'), 'Tormenta');
    typeInto(input('system-content-version'), '1.0');
    await fixture.whenStable();

    expect(element.querySelector('#system-content-version-msg')?.textContent).toContain('MAJOR.MINOR.PATCH');
    expect(submitButton().disabled).toBe(true);
  });

  it('emite o rascunho válido ao submeter', async () => {
    typeInto(input('system-name'), 'Tormenta');
    typeInto(element.querySelector<HTMLSelectElement>('#system-sync-policy')!, 'APPLY_TO_NEXT_SESSION');
    await fixture.whenStable();

    submitButton().click();
    await fixture.whenStable();

    expect(host.saved).toHaveLength(1);
    expect(host.saved[0].name).toBe('Tormenta');
    expect(host.saved[0].defaultSyncPolicy).toBe('APPLY_TO_NEXT_SESSION');
  });

  it('recarrega o modelo quando o valor inicial muda (edição)', async () => {
    host.initial.set({ ...EMPTY_DRAFT, name: 'Carregado', contentVersion: '3.1.4' });
    await fixture.whenStable();

    expect(input('system-name').value).toBe('Carregado');
    expect(input('system-content-version').value).toBe('3.1.4');
  });

  it('bloqueia o envio enquanto ocupado', async () => {
    host.initial.set({ ...EMPTY_DRAFT, name: 'Tormenta' });
    host.busy.set(true);
    await fixture.whenStable();

    expect(submitButton().disabled).toBe(true);
    expect(submitButton().textContent).toContain('Forjando');
  });
});
