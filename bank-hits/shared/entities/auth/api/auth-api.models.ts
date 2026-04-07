/** SSO POST /auth/login */
export interface SsoLoginRequest {
  username: string;
  password: string;
}

/** SSO POST /auth/register */
export interface SsoRegisterRequest {
  name: string;
  username: string;
  password: string;
  roles?: ('CLIENT' | 'EMPLOYEE')[];
}

/** SSO token response */
export interface SsoTokenResponse {
  token: string;
  expirySeconds: number;
  tokenType: string;
}

/** BFF GET /bff/client/profile */
export interface ClientProfileResponse {
  userId: string;
  name: string;
  email: string;
  status: 'ACTIVE' | 'BANNED';
}

export type AuthUserRole = 'CLIENT' | 'EMPLOYEE';
export type AuthStoredRole = 'client' | 'employee';

// Legacy aliases for backward compat with existing guards
export type AuthTokenResponse = SsoTokenResponse;
export type AuthLoginRequest = SsoLoginRequest;
export type AuthRegisterRequest = SsoRegisterRequest;
export interface AuthUserFullResponse {
  id: string;
  name: string;
  email: string;
  status: 'ACTIVE' | 'BANNED';
  role: AuthUserRole;
}
