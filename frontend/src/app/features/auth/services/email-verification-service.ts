import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { API_URL } from '../../../core/services/api.service';



export type OtpPurpose = 'EMAIL_VERIFICATION' | 'TWO_FACTOR_AUTH';

export interface OtpVerifyRequest {
  readonly email: string;
  readonly code: string;
}

export interface OtpSendRequest {
  readonly email: string;
}

export interface OtpVerifyResponse {
  readonly verified: boolean;
}


@Injectable({
  providedIn: 'root',
})
export class EmailVerificationService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = inject(API_URL);

  public sendEmailVerification(email: string) {
    const body: OtpSendRequest = { email };
    return this.http.post<void>(`${this.apiUrl}/api/email-verification/send`, body);
  }

  public verifyEmail(email: string, code: string) {
    const body: OtpVerifyRequest = { email, code };
    return this.http.post<OtpVerifyResponse>(`${this.apiUrl}/api/email-verification/verify`, body);
  }

  public send2fa(email: string) {
    const body: OtpSendRequest = { email };
    return this.http.post<void>(`${this.apiUrl}/api/2fa/send`, body);
  }

  public verify2fa(email: string, code: string) {
    const body: OtpVerifyRequest = { email, code };
    return this.http.post<OtpVerifyResponse>(`${this.apiUrl}/api/2fa/verify`, body);
  }
}
