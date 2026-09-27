import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserResponse, PromotionResponse, RegisterRequest } from '../user.models';
import { API_URL } from '../../../core/services/api.service';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = inject(API_URL);

  private get base(): string {
    return `${this.apiUrl}/api/users`;
  }

  register(body: RegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.base, body);
  }

  public findAll(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(this.base);
  }

  public findById(id: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.base}/${id}`);
  }

  public findByEmail(email: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.base}/email/${encodeURIComponent(email)}`);
  }

  public delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  public promoteToAdmin(id: string): Observable<PromotionResponse> {
    return this.http.put<PromotionResponse>(`${this.base}/promote/${id}/to-admin`, {});
  }

  public promoteToDev(id: string): Observable<PromotionResponse> {
    return this.http.put<PromotionResponse>(`${this.base}/promote/${id}/to-dev`, {});
  }
}
