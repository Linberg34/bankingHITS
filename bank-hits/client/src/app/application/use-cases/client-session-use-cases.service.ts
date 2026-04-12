import { Injectable, inject } from '@angular/core';
import { AuthApiService } from 'shared/entities/auth';
import { API_BASE_URL } from 'shared/api';
import { FcmTokenService } from 'shared/frontend-core';

const SSO_LOGIN_URL = 'http://localhost:4202/login';

@Injectable({ providedIn: 'root' })
export class ClientSessionUseCasesService {
  private readonly authApi = inject(AuthApiService);
  private readonly fcmTokenService = inject(FcmTokenService);
  private readonly apiBaseUrl = inject(API_BASE_URL);

  logout(): void {
    void this.fcmTokenService
      .unregisterToken(this.apiBaseUrl)
      .finally(() => {
        this.authApi.clearAuth();
        const returnUrl = `${window.location.origin}/auth/callback`;
        window.location.href = `${SSO_LOGIN_URL}?returnUrl=${encodeURIComponent(returnUrl)}`;
      });
  }

  clearSession(): void {
    this.authApi.clearAuth();
  }
}
