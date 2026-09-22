import { UserResponse } from "../admin/user.models";

export type AccountType = 'COMMON' | 'ADMIN' | 'DEV';

export interface LoginRequest {
  readonly email: string;
  readonly password: string;
}

export interface LoginResponse {
  readonly accessToken: string;
  readonly tokenType: string;
  readonly expiresIn: number;
  readonly user: UserResponse;
  readonly type: AccountType;
}

export interface RefreshTokenResponse {
  readonly accessToken: string;
}