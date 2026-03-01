import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import type { LoginRequest, LoginResponse, UserMe } from '../models/auth.types';
import {BASE_URL, TOKEN_KEY} from '../../../../../shared/api/secrets';
@Injectable({ providedIn: 'root' })
export class AuthApiService {
  constructor(private readonly http: HttpClient) {}

  login(email: string): Observable<LoginResponse> {
    const body: LoginRequest = { email };
    return this.http
      .post<LoginResponse>(`${BASE_URL}/api/auth/login`, body, {
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      })
      .pipe(tap((res) => this.setToken(res.token)));
  }

  getMe(): Observable<UserMe> {
    const token = this.getToken();
    const headers = new HttpHeaders({
      ...(token && { Authorization: `Bearer ${token}` }),
    });
    return this.http.get<UserMe>(`${BASE_URL}/api/users/me`, { headers });
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  clearToken(): void {
    localStorage.removeItem(TOKEN_KEY);
  }
}
