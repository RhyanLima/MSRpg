import { ApplicationRef, signal } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RpgSystemService } from './rpg-system.service';
import { API, SYSTEM_ID, aSummary, aSystem } from '../testing/system-testing';
import { RpgSystemFilter, toRequest, EMPTY_DRAFT } from '../models/rpg-system.model';

describe('RpgSystemService', () => {
  let service: RpgSystemService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(RpgSystemService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('listResource envia o filtro de nome e expõe o resultado como signal', async () => {
    const filter = signal<RpgSystemFilter>({ name: ' dark ' });
    const systems = TestBed.runInInjectionContext(() => service.listResource(filter));
    TestBed.tick();

    const request = httpMock.expectOne(r => r.url === API);
    expect(request.request.params.get('name')).toBe('dark');
    request.flush([aSummary()]);
    await TestBed.inject(ApplicationRef).whenStable();

    expect(systems.value()).toHaveLength(1);
  });

  it('listResource sem filtro não envia query params', () => {
    TestBed.runInInjectionContext(() => service.listResource(signal({})));
    TestBed.tick();

    const request = httpMock.expectOne(API);
    expect(request.request.params.keys()).toEqual([]);
    request.flush([]);
  });

  it('detailResource não faz requisição enquanto o id é undefined', () => {
    TestBed.runInInjectionContext(() => service.detailResource(signal<string | undefined>(undefined)));
    TestBed.tick();

    httpMock.expectNone(() => true);
  });

  it('create faz POST com o corpo da requisição', () => {
    const body = toRequest({ ...EMPTY_DRAFT, name: 'Tormenta' });

    service.create(body).subscribe();

    const request = httpMock.expectOne(API);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(body);
    request.flush(aSystem());
  });

  it('update faz PUT e delete faz DELETE no caminho do id', () => {
    service.update(SYSTEM_ID, toRequest({ ...EMPTY_DRAFT, name: 'Tormenta' })).subscribe();
    const put = httpMock.expectOne(`${API}/${SYSTEM_ID}`);
    expect(put.request.method).toBe('PUT');
    put.flush(aSystem());

    service.delete(SYSTEM_ID).subscribe();
    const del = httpMock.expectOne(`${API}/${SYSTEM_ID}`);
    expect(del.request.method).toBe('DELETE');
    del.flush(null);
  });

  it('codifica o id: caracteres especiais não alteram o caminho', () => {
    service.delete('../admin?x=1').subscribe();

    const request = httpMock.expectOne(`${API}/..%2Fadmin%3Fx%3D1`);
    request.flush(null);
  });
});
