import { AccountType } from "../auth/auth.models";

export interface UserResponse {
  readonly id: string;
  readonly name: string;
  readonly birthDate: string;
  readonly email: string;
}

export interface RegisterRequest {
  readonly name: string;
  readonly birthDate: string;
  readonly email: string;
  readonly password: string;
}

export interface PagedResponse<T> {
  readonly content: T[];
  readonly totalElements: number;
  readonly totalPages: number;
  readonly number: number;
  readonly size: number;
}

export interface PromotionResponse {
  readonly userId: string;
  readonly previousRole: AccountType;
  readonly currentRole: AccountType;
  readonly success: boolean;
}