import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { EditSystem } from './edit-system';
import { API, SYSTEM_ID, aSystem, stubShellDecorations, typeInto } from '../../testing/system-testing';

describe('EditSystem', () => {
  let httpMock: HttpTestingController;
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([
          { path: 'systems/:id', component: EditSystem },
          { path: 'systems', children: [] },
        ]),
      ],
    });
    stubShellDecorations();
    httpMock = TestBed.inject(HttpTestingController);
    harness = await RouterTestingHarness.create();
  });

  afterEach(() => httpMock.verify());

  async function open(id: string): Promise<HTMLElement> {
    await harness.navigateByUrl(`/systems/${id}`);
    TestBed.tick();
    return harness.routeNativeElement!;
  }

  it('carrega o sistema, exibe a engine (somente leitura) e preenche o formulário', async () => {
    const element = await open(SYSTEM_ID);
    httpMock.expectOne(`${API}/${SYSTEM_ID}`).flush(aSystem({ engineVersion: '0.4.2', contentVersion: '2.0.0' }));
    await harness.fixture.whenStable();

    expect(element.querySelector('.facts')?.textContent).toContain('0.4.2');
    expect(element.querySelector<HTMLInputElement>('#system-name')!.value).toBe('Tormenta');
    expect(element.querySelector<HTMLInputElement>('#system-content-version')!.value).toBe('2.0.0');
    expect(element.querySelector('#system-engine-version')).toBeNull();
  });

  it('salva com PUT e volta para a lista', async () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const element = await open(SYSTEM_ID);
    httpMock.expectOne(`${API}/${SYSTEM_ID}`).flush(aSystem());
    await harness.fixture.whenStable();

    typeInto(element.querySelector<HTMLInputElement>('#system-name')!, 'Tormenta 20');
    await harness.fixture.whenStable();
    element.querySelector<HTMLButtonElement>('button[type="submit"]')!.click();
    await harness.fixture.whenStable();

    const put = httpMock.expectOne(`${API}/${SYSTEM_ID}`);
    expect(put.request.method).toBe('PUT');
    expect(put.request.body.name).toBe('Tormenta 20');
    put.flush(aSystem({ name: 'Tormenta 20' }));
    await harness.fixture.whenStable();

    expect(navigate).toHaveBeenCalledWith(['/systems']);
  });

  it('id fora do formato UUID não gera requisição', async () => {
    const element = await open('nao-e-uuid');
    await harness.fixture.whenStable();

    httpMock.expectNone(() => true);
    expect(element.querySelector('[role="alert"]')?.textContent).toContain('inválido');
  });

  it('404 mostra mensagem de sistema não encontrado', async () => {
    const element = await open(SYSTEM_ID);
    httpMock.expectOne(`${API}/${SYSTEM_ID}`).flush(null, { status: 404, statusText: 'Not Found' });
    await harness.fixture.whenStable();

    expect(element.querySelector('[role="alert"]')?.textContent).toContain('não encontrado');
  });
});
