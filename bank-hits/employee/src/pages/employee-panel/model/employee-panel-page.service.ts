import { Injectable, inject } from '@angular/core';
import { API_BASE_URL } from 'shared/api';
import { FcmTokenService } from 'shared/frontend-core';
import { EmployeeAdminRequestService } from '../../../app/infrastructure/request/employee-admin-request.service';

const SSO_LOGIN_URL = 'http://localhost:4202/login';

@Injectable({
  providedIn: 'root',
})
export class EmployeePanelPageService {
  private readonly fcmTokenService = inject(FcmTokenService);
  private readonly apiBaseUrl = inject(API_BASE_URL);

  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  logout(): void {
    void this.fcmTokenService
      .unregisterToken(this.apiBaseUrl)
      .finally(() => {
        this.requestService.clearAuth();
        const returnUrl = `${window.location.origin}/auth/callback`;
        window.location.href = `${SSO_LOGIN_URL}?returnUrl=${encodeURIComponent(returnUrl)}`;
      });
  }
}
