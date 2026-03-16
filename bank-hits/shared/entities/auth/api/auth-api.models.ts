export interface AuthRegisterRequest {
  name: string;
  email: string;
}

export interface AuthLoginRequest {
  email: string;
}

export interface AuthTokenResponse {
  token: string;
}

export type AuthUserRole = 'CLIENT' | 'EMPLOYEE';
export type AuthStoredRole = 'client' | 'employee';
export type AuthUserStatus = 'ACTIVE' | 'INACTIVE' | 'BANNED';

export interface AuthUserFullResponse {
  id: number | string;
  name: string;
  email: string;
  status: AuthUserStatus;
  registeredAt: string;
  role: AuthUserRole;
}
