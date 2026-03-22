import { Injectable, inject } from '@angular/core';
import { AuthApiService } from 'shared/entities/auth';

const SSO_LOGIN_URL = 'http://localhost:4202/login';

@Injectable({ providedIn: 'root' })
export class ClientSessionUseCasesService {
  private readonly authApi = inject(AuthApiService);

  logout(): void {
    this.authApi.clearAuth();
    const returnUrl = `${window.location.origin}/auth/callback`;
    window.location.href = `${SSO_LOGIN_URL}?returnUrl=${encodeURIComponent(returnUrl)}`;
  }

  clearSession(): void {
    this.authApi.clearAuth();
  }
}
