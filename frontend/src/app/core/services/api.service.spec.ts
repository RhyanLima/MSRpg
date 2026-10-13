import { TestBed } from '@angular/core/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiService);
  });

  afterEach(() => {
    delete window.msrpg;
  });

  it('usa a ponte do Electron (window.msrpg) quando disponível', async () => {
    window.msrpg = { getServerInfo: () => Promise.resolve({ port: 41234, token: 'abc' }) };

    await service.initialize();

    expect(service.backendOrigin).toBe('http://localhost:41234');
    expect(service.sessionToken).toBe('abc');
  });

  it('cai no fallback de desenvolvimento sem a ponte', async () => {
    await service.initialize();

    expect(service.backendOrigin).toBe('http://localhost:8080');
    expect(service.sessionToken).toBe('');
  });

  it('é idempotente: consulta a ponte uma única vez', async () => {
    const getServerInfo = vi.fn(() => Promise.resolve({ port: 9000, token: 't' }));
    window.msrpg = { getServerInfo };

    await Promise.all([service.initialize(), service.initialize()]);

    expect(getServerInfo).toHaveBeenCalledTimes(1);
  });

  it('rejeita porta inválida vinda da ponte', async () => {
    window.msrpg = { getServerInfo: () => Promise.resolve({ port: 70000, token: '' }) };

    await expect(service.initialize()).rejects.toThrow(/port/i);
  });

  it('só monta URLs sob /api/ e reconhece apenas a própria origem', () => {
    expect(service.backendUrl('/api/v1/x')).toBe('http://localhost:8080/api/v1/x');
    expect(() => service.backendUrl('/etc/passwd')).toThrow();
    expect(service.isBackendUrl('http://localhost:8080/api/v1/x')).toBe(true);
    expect(service.isBackendUrl('http://localhost:8080.evil.com/api')).toBe(false);
    expect(service.isBackendUrl('https://example.com/api')).toBe(false);
  });
});
