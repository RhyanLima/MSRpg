import { computed, inject, Injectable, signal } from '@angular/core';
import { AccountType, LoginRequest, LoginResponse } from '../auth.models';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../../core/services/api.service';
import { UserResponse } from '../../admin/user.models';

/** Estado interno da autenticação */
interface AuthState {
  readonly user: UserResponse | null;
  readonly accessToken: string | null;
  readonly accountType: AccountType | null;
}

const INITIAL_STATE: AuthState = {
  user: null,
  accessToken: null,
  accountType: null,
};

const TOKEN_STORAGE_KEY = 'msr_access_token';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = inject(API_URL);

  private readonly _state = signal<AuthState>(this.restoreState());

  public readonly user = computed(() => this._state().user);

  public readonly accessToken = computed(() => this._state().accessToken);

  public readonly accountType = computed(() => this._state().accountType);

  public readonly isAuthenticated = computed(() => this._state().accessToken !== null);

  public readonly isAdmin = computed(() => {
    const type = this._state().accountType;
    return type === 'ADMIN' || type === 'DEV';
  });

  public readonly isDev = computed(() => this._state().accountType === 'DEV');

  public readonly userInitials = computed(() => {
    const name = this._state().user?.name ?? '';
    return name
      .split(' ')
      .slice(0, 2)
      .map((n) => n[0]?.toUpperCase() ?? '')
      .join('');
  });

  public async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await firstValueFrom(
      this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/login`, credentials)
    );
    this.applyLoginResponse(response);
    return response;
  }

  public async logout(): Promise<void> {
    try {
      await firstValueFrom(
        this.http.post<void>(`${this.apiUrl}/api/auth/logout`, {})
      );
    } finally {
      this.clearState();
      this.router.navigate(['/login']);
    }
  }

  public async logoutAll(): Promise<void> {
    try {
      await firstValueFrom(
        this.http.post<void>(`${this.apiUrl}/api/auth/logout-all`, {})
      );
    } finally {
      this.clearState();
      this.router.navigate(['/login']);
    }
  }

  // Via cookie HttpOnly.
  public async refreshAccessToken(): Promise<boolean> {
    try {
      const response = await firstValueFrom(
        this.http.post<{ accessToken: string }>(`${this.apiUrl}/api/auth/refresh`, {})
      );
      this._state.update((s) => ({ ...s, accessToken: response.accessToken }));
      sessionStorage.setItem(TOKEN_STORAGE_KEY, response.accessToken);
      return true;
    } catch {
      this.clearState();
      return false;
    }
  }

  private applyLoginResponse(response: LoginResponse): void {
    const newState: AuthState = {
      user: response.user,
      accessToken: response.accessToken,
      accountType: response.type,
    };
    this._state.set(newState);
    sessionStorage.setItem(TOKEN_STORAGE_KEY, response.accessToken);
  }

  private clearState(): void {
    this._state.set(INITIAL_STATE);
    sessionStorage.removeItem(TOKEN_STORAGE_KEY);
  }

  private restoreState(): AuthState {
    const token = sessionStorage.getItem(TOKEN_STORAGE_KEY);
    if (!token) return INITIAL_STATE;
    return { ...INITIAL_STATE, accessToken: token };
  }

}
