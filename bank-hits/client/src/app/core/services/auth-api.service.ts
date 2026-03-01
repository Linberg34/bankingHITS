import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import type { LoginRequest, LoginResponse, UserMe } from '../models/auth.types';
import { API_BASE_URL, AUTH_TOKEN_STORAGE_KEY } from '../../../../../shared/api';

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(API_BASE_URL);

  login(email: string): Observable<LoginResponse> {
    const body: LoginRequest = { email };
    return this.http
      .post<LoginResponse>(`${this.baseUrl}/api/auth/login`, body, {
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      })
      .pipe(tap((res) => this.setToken(res.token)));
  }

  getMe(): Observable<UserMe> {
    return this.http.get<UserMe>(`${this.baseUrl}/api/users/me`);
  }

  logout(): Observable<string> {
    return this.http.post(`${this.baseUrl}/api/auth/logout`, null, {
      responseType: 'text',
    }).pipe(
      tap(() => this.clearToken())
    );
  }

  getToken(): string | null {
    return localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
  }

  setToken(token: string): void {
    localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, token);
  }

  clearToken(): void {
    localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
  }
}
