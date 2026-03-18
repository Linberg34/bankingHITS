import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AuthApiService,
  type AuthTokenResponse,
  type AuthUserFullResponse,
} from 'shared/entities/auth';

@Injectable({ providedIn: 'root' })
export class ClientAuthRequestService {
  private readonly authApi = inject(AuthApiService);

  login(email: string): Observable<AuthTokenResponse> {
    return this.authApi.login({ email });
  }

  getCurrentUser(): Observable<AuthUserFullResponse> {
    return this.authApi.getCurrentUser();
  }

  logout(): Observable<string> {
    return this.authApi.logout();
  }

  clearAuth(): void {
    this.authApi.clearAuth();
  }
}

