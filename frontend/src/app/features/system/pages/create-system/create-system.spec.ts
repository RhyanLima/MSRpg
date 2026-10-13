import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { CreateSystem } from './create-system';
import { API, aSystem, stubShellDecorations, typeInto } from '../../testing/system-testing';

describe('CreateSystem', () => {
  let fixture: ComponentFixture<CreateSystem>;
  let element: HTMLElement;
  let httpMock: HttpTestingController;
  let navigate: ReturnType<typeof vi.spyOn>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    stubShellDecorations();
    httpMock = TestBed.inject(HttpTestingController);
    navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(CreateSystem);
    element = fixture.nativeElement;
    await fixture.whenStable();
  });

  afterEach(() => httpMock.verify());

  async function fillAndSubmit(name: string): Promise<void> {
    typeInto(element.querySelector<HTMLInputElement>('#system-name')!, name);
    await fixture.whenStable();
    element.querySelector<HTMLButtonElement>('button[type="submit"]')!.click();
    await fixture.whenStable();
  }

  it('envia apenas campos editáveis (sem engineVersion) e volta para a lista', async () => {
    await fillAndSubmit('  Tormenta  ');

    const request = httpMock.expectOne(API);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      name: 'Tormenta',
      description: '',
      contentVersion: '1.0.0',
      defaultSyncPolicy: 'APPLY_TO_NEW_ONLY',
      missingComponentPolicy: 'WARN_AND_SKIP_STEP',
      conflictResolutionStrategy: 'ASK_USER',
    });
    request.flush(aSystem());
    await fixture.whenStable();

    expect(navigate).toHaveBeenCalledWith(['/systems']);
  });

  it('mostra mensagem de nome duplicado em 409 e permanece na tela', async () => {
    await fillAndSubmit('Tormenta');

    httpMock.expectOne(API).flush({ message: 'exists' }, { status: 409, statusText: 'Conflict' });
    await fixture.whenStable();

    expect(element.querySelector('[role="alert"]')?.textContent).toContain('Já existe um sistema com esse nome');
    expect(navigate).not.toHaveBeenCalled();
  });
});
