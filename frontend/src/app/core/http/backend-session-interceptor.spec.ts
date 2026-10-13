import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ApiService } from '../services/api.service';
import { backendSessionInterceptor } from './backend-session-interceptor';

describe('backendSessionInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    window.msrpg = { getServerInfo: () => Promise.resolve({ port: 8080, token: 'secret-token' }) };
    TestBed.configureTestingModule({
      providers: [provideHttpClient(withInterceptors([backendSessionInterceptor])), provideHttpClientTesting()],
    });
    await TestBed.inject(ApiService).initialize();
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    delete window.msrpg;
  });

  it('anexa o token nas chamadas ao backend local', () => {
    http.get('http://localhost:8080/api/v1/rpg-systems').subscribe();

    const request = httpMock.expectOne('http://localhost:8080/api/v1/rpg-systems');
    expect(request.request.headers.get('X-Session-Token')).toBe('secret-token');
    request.flush([]);
  });

  it('nunca envia o token para outra origem', () => {
    http.get('https://example.com/api/data').subscribe();

    const request = httpMock.expectOne('https://example.com/api/data');
    expect(request.request.headers.has('X-Session-Token')).toBe(false);
    request.flush({});
  });
});
