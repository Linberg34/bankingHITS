import { Injectable } from '@angular/core';
import { AuthStoredRole, AuthUserRole } from './auth-api.models';

const AUTH_TOKEN_STORAGE_KEY = 'auth_token';
const AUTH_ROLE_STORAGE_KEY = 'auth_role';

/**
 * Manages JWT token storage received from SSO service.
 * Authentication itself happens on the SSO page (port 4202).
 * This service only handles local token state.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthApiService {

  getToken(): string | null {
    try {
      return globalThis.localStorage?.getItem(AUTH_TOKEN_STORAGE_KEY) ?? null;
    } catch {
      return null;
    }
  }

  setToken(token: string): void {
    localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, token);
  }

  clearToken(): void {
    try {
      localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
    } catch {
      // ignore
    }
  }

  getRole(): AuthStoredRole | null {
    try {
      const role = localStorage.getItem(AUTH_ROLE_STORAGE_KEY);
      if (role === 'client' || role === 'employee') {
        return role;
      }
    } catch {
      // ignore
    }
    return null;
  }

  setRole(role: AuthUserRole | AuthStoredRole): void {
    try {
      localStorage.setItem(AUTH_ROLE_STORAGE_KEY, role.toLowerCase());
    } catch {
      // ignore
    }
  }

  clearAuth(): void {
    try {
      localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
      localStorage.removeItem(AUTH_ROLE_STORAGE_KEY);
    } catch {
      // ignore
    }
  }

  /**
   * Reads JWT claims to extract role without making an HTTP call.
   * BFF JWT contains 'roles' claim as array.
   */
  extractRoleFromToken(token: string): AuthStoredRole | null {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const roles: string[] = payload['roles'] ?? [];
      if (roles.some((r) => r.toUpperCase() === 'EMPLOYEE')) {
        return 'employee';
      }
      if (roles.some((r) => r.toUpperCase() === 'CLIENT')) {
        return 'client';
      }
    } catch {
      // invalid token
    }
    return null;
  }
}
