import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable, map, switchMap, tap } from 'rxjs';
import { API_BASE_URL } from '../../../api';
import {
  AuthStoredRole,
  AuthLoginRequest,
  AuthRegisterRequest,
  AuthTokenResponse,
  AuthUserFullResponse,
  AuthUserRole,
} from './auth-api.models';

const AUTH_TOKEN_STORAGE_KEY = 'auth_token';
const AUTH_ROLE_STORAGE_KEY = 'auth_role';
const LEGACY_AUTH_ROLE_STORAGE_KEY = 'hitsbank_user_role';
const AUTH_CONTROLLER_PREFIX = '/api/auth';

@Injectable({
  providedIn: 'root',
})
export class AuthApiService {
  constructor(
    private readonly httpClient: HttpClient,
    @Inject(API_BASE_URL) private readonly apiBaseUrl: string
  ) {}

  register(payload: AuthRegisterRequest): Observable<AuthTokenResponse> {
    return this.registerRequest(payload, false, true);
  }

  registerEmployee(payload: AuthRegisterRequest): Observable<AuthTokenResponse> {
    return this.registerRequest(payload, true, true);
  }

  registerWithoutAuth(payload: AuthRegisterRequest): Observable<AuthTokenResponse> {
    return this.registerRequest(payload, false, false);
  }

  registerEmployeeWithoutAuth(payload: AuthRegisterRequest): Observable<AuthTokenResponse> {
    return this.registerRequest(payload, true, false);
  }

  login(payload: AuthLoginRequest): Observable<AuthTokenResponse> {
    return this.httpClient
      .post<AuthTokenResponse>(`${this.normalizedBaseUrl}${AUTH_CONTROLLER_PREFIX}/login`, payload)
      .pipe(switchMap((response) => this.storeAuthFromToken(response)));
  }

  getCurrentUser(): Observable<AuthUserFullResponse> {
    return this.httpClient.get<AuthUserFullResponse>(`${this.normalizedBaseUrl}/api/users/me`);
  }

  logout(): Observable<string> {
    return this.httpClient.post(`${this.normalizedBaseUrl}${AUTH_CONTROLLER_PREFIX}/logout`, null, {
      responseType: 'text',
    });
  }

  getToken(): string | null {
    return localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
  }

  clearToken(): void {
    localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
  }

  getRole(): AuthStoredRole | null {
    const storedRole = localStorage.getItem(AUTH_ROLE_STORAGE_KEY);
    if (storedRole === 'client' || storedRole === 'employee') {
      return storedRole;
    }
    const legacyRole = localStorage.getItem(LEGACY_AUTH_ROLE_STORAGE_KEY);
    if (legacyRole === 'client' || legacyRole === 'employee') {
      return legacyRole;
    }
    return null;
  }

  setRole(role: AuthUserRole | AuthStoredRole): void {
    this.saveRole(role);
  }

  clearAuth(): void {
    this.clearToken();
    localStorage.removeItem(AUTH_ROLE_STORAGE_KEY);
    localStorage.removeItem(LEGACY_AUTH_ROLE_STORAGE_KEY);
  }

  private get normalizedBaseUrl(): string {
    return this.apiBaseUrl.replace(/\/+$/, '');
  }

  private saveToken(token: string): void {
    localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, token);
  }

  private saveRole(role: AuthUserRole | AuthStoredRole): void {
    const normalizedRole = role.toLowerCase() as AuthStoredRole;
    localStorage.setItem(AUTH_ROLE_STORAGE_KEY, normalizedRole);
  }

  private storeAuthFromToken(response: AuthTokenResponse): Observable<AuthTokenResponse> {
    this.saveToken(response.token);
    return this.getCurrentUser().pipe(
      tap((user) => this.saveRole(user.role)),
      map(() => response)
    );
  }

  private registerRequest(
    payload: AuthRegisterRequest,
    employee: boolean,
    persistAuth: boolean
  ): Observable<AuthTokenResponse> {
    const endpoint = employee ? '/register/employee' : '/register';
    const request$ = this.httpClient.post<AuthTokenResponse>(
      `${this.normalizedBaseUrl}${AUTH_CONTROLLER_PREFIX}${endpoint}`,
      payload
    );

    if (!persistAuth) {
      return request$;
    }

    return request$.pipe(switchMap((response) => this.storeAuthFromToken(response)));
  }
}
