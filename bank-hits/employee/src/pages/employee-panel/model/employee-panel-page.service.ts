import { Injectable } from '@angular/core';
import { EmployeeAdminRequestService } from '../../../app/infrastructure/request/employee-admin-request.service';

const SSO_LOGIN_URL = 'http://localhost:4202/login';

@Injectable({
  providedIn: 'root',
})
export class EmployeePanelPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  logout(): void {
    this.requestService.clearAuth();
    const returnUrl = `${window.location.origin}/auth/callback`;
    window.location.href = `${SSO_LOGIN_URL}?returnUrl=${encodeURIComponent(returnUrl)}`;
  }
}
