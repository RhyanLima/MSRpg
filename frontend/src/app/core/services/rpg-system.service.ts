import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

export interface RpgSystemSummary {
  id: string;
  name: string;
  description?: string;
  engineVersion: string;
  contentVersion: string;
  syncPolicy: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateRpgSystemRequest {
  name: string;
  description: string;
  engineVersion: string;
  contentVersion: string;
  syncPolicy: string;
  settingsJson: string;
}


@Service()
export class RpgSystemService {

  private http = inject(HttpClient);
  private apiService = inject(ApiService);

  private get baseUrl(): string {
    const port = this.apiService.getServerInfo?.port || 8080;
    return `http://localhost:${port}/api/v1/rpg-systems`;
  }

  private get headers(): HttpHeaders {
    const token = this.apiService.getServerInfo?.token || '';
    return new HttpHeaders({
      'X-Session-Token': token,
    });
  }

  public listSystems(): Observable<RpgSystemSummary[]> {
    return this.http.get<RpgSystemSummary[]>(this.baseUrl, { headers: this.headers });
  }

  public createSystem(request: CreateRpgSystemRequest): Observable<any> {
    return this.http.post(this.baseUrl, request, { headers: this.headers });
  }
}
