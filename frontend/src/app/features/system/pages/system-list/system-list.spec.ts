import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { SystemList } from './system-list';
import { RpgSystemSummary } from '../../models/rpg-system.model';
import { API, SYSTEM_ID, aSummary, stubShellDecorations } from '../../testing/system-testing';

describe('SystemList', () => {
  let fixture: ComponentFixture<SystemList>;
  let element: HTMLElement;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    stubShellDecorations();
    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(SystemList);
    element = fixture.nativeElement;
  });

  afterEach(() => httpMock.verify());

  async function respondList(body: readonly RpgSystemSummary[]): Promise<void> {
    TestBed.tick();
    httpMock.expectOne(r => r.url === API && r.method === 'GET').flush([...body]);
    await fixture.whenStable();
  }

  it('lista os sistemas com versões e link de edição', async () => {
    await respondList([aSummary(), aSummary({ id: 'b', name: 'Dark Fantasy', description: null })]);

    const rows = element.querySelectorAll('.row');
    expect(rows).toHaveLength(2);
    expect(rows[0].textContent).toContain('Tormenta');
    expect(rows[0].textContent).toContain('engine 0.1.0');
    expect(rows[0].querySelector('a.row-name')?.getAttribute('href')).toBe(`/systems/${SYSTEM_ID}`);
  });

  it('mostra estado vazio com chamada para criar', async () => {
    await respondList([]);

    expect(element.querySelector('.empty')?.textContent).toContain('Nenhum sistema forjado');
  });

  it('mostra erro de carregamento com opção de tentar novamente', async () => {
    TestBed.tick();
    httpMock.expectOne(API).flush(null, { status: 0, statusText: 'Unknown Error' });
    await fixture.whenStable();

    expect(element.querySelector('.alert')?.textContent).toContain('Servidor indisponível');
  });

  it('busca por nome apenas ao submeter', async () => {
    await respondList([aSummary()]);

    const search = element.querySelector<HTMLInputElement>('#system-search')!;
    search.value = 'dark';
    search.dispatchEvent(new Event('input'));
    TestBed.tick();
    httpMock.expectNone(() => true);

    element.querySelector<HTMLFormElement>('form.search')!.dispatchEvent(new Event('submit'));
    TestBed.tick();
    const request = httpMock.expectOne(r => r.url === API);
    expect(request.request.params.get('name')).toBe('dark');
    request.flush([]);
    await fixture.whenStable();

    expect(element.querySelector('.empty')?.textContent).toContain('Nenhum sistema encontrado');
  });

  it('exclui após confirmação e recarrega a lista', async () => {
    await respondList([aSummary()]);

    element.querySelector<HTMLButtonElement>('.btn--danger')!.click();
    await fixture.whenStable();
    const dialog = element.querySelector('app-confirm-dialog')!;
    expect(dialog.textContent).toContain('Tormenta');

    dialog.querySelectorAll('button')[1].click();
    const del = httpMock.expectOne(`${API}/${SYSTEM_ID}`);
    expect(del.request.method).toBe('DELETE');
    del.flush(null);
    await respondList([]);

    expect(element.querySelector('app-confirm-dialog')).toBeNull();
    expect(element.querySelector('.empty')).not.toBeNull();
  });

  it('explica o 409 quando o sistema ainda tem conteúdo', async () => {
    await respondList([aSummary()]);

    element.querySelector<HTMLButtonElement>('.btn--danger')!.click();
    await fixture.whenStable();
    element.querySelector('app-confirm-dialog')!.querySelectorAll('button')[1].click();
    httpMock.expectOne(`${API}/${SYSTEM_ID}`).flush({ message: 'in use' }, { status: 409, statusText: 'Conflict' });
    await respondList([aSummary()]);

    expect(element.querySelector('.alert')?.textContent).toContain('ainda possui conteúdo');
  });
});
