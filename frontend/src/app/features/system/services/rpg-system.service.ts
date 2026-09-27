import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable, Service } from '@angular/core';
import { ApiService } from '../../../core/services/api.service';
import { Observable } from 'rxjs';
import { RpgSystemSummary } from '../models/rpg-system.model';

export interface CreateRpgSystemRequest {
  readonly name: string;
  readonly description: string;
  readonly engineVersion: string;
  readonly contentVersion: string;
  readonly defaultResolutionPolicyId: string;
  readonly syncPolicy: string;
  readonly settingsJson: string;
}

export interface CreateRpgSystemResponse {
  readonly id: string;
}


@Injectable({
  providedIn: 'root'
})
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

  public createSystem(request: CreateRpgSystemRequest): Observable<CreateRpgSystemResponse> {
    return this.http.post<CreateRpgSystemResponse>(this.baseUrl, request, { headers: this.headers });
  }
}
