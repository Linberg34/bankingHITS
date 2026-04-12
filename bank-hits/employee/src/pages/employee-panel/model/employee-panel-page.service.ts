import { Injectable, inject } from '@angular/core';
import { FcmTokenService } from 'shared/frontend-core';
import { EmployeeAdminRequestService } from '../../../app/infrastructure/request/employee-admin-request.service';

const SSO_LOGIN_URL = 'http://localhost:4202/login';

@Injectable({
  providedIn: 'root',
})
export class EmployeePanelPageService {
  private readonly fcmTokenService = inject(FcmTokenService);

  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  logout(): void {
    void this.fcmTokenService
      .unregisterToken('http://sof-kov.ru:8085/bff/employee')
      .finally(() => {
        this.requestService.clearAuth();
        const returnUrl = `${window.location.origin}/auth/callback`;
        window.location.href = `${SSO_LOGIN_URL}?returnUrl=${encodeURIComponent(returnUrl)}`;
      });
  }
}
