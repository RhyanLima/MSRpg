import { HttpClient, httpResource } from '@angular/common/http';
import { Service, Signal, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { RpgSystem, RpgSystemFilter, RpgSystemRequest, RpgSystemSummary } from '../models/rpg-system.model';

const RESOURCE_PATH = '/api/v1/rpg-systems';

@Service()
export class RpgSystemService {

  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);

  public listResource(filter: Signal<RpgSystemFilter>) {
    return httpResource<RpgSystemSummary[]>(
      () => ({ url: this.url(), params: queryParams(filter()) }),
      { defaultValue: [] },
    );
  }

  public detailResource(id: Signal<string | undefined>) {
    return httpResource<RpgSystem>(() => {
      const value = id();
      return value ? this.url(value) : undefined;
    });
  }

  public listSystems(): Observable<RpgSystemSummary[]> {
    return this.http.get<RpgSystemSummary[]>(this.url());
  }

  public create(request: RpgSystemRequest): Observable<RpgSystem> {
    return this.http.post<RpgSystem>(this.url(), request);
  }

  public update(id: string, request: RpgSystemRequest): Observable<RpgSystem> {
    return this.http.put<RpgSystem>(this.url(id), request);
  }

  public delete(id: string): Observable<void> {
    return this.http.delete<void>(this.url(id));
  }

  private url(id?: string): string {
    const suffix = id === undefined ? '' : `/${encodeURIComponent(id)}`;
    return this.api.backendUrl(`${RESOURCE_PATH}${suffix}`);
  }
}

function queryParams(filter: RpgSystemFilter): Record<string, string> {
  const name = filter.name?.trim();
  return name ? { name } : {};
}
