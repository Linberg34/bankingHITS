import { Injectable } from '@angular/core';
import { EmployeeAdminRequestService } from '../../../app/infrastructure/request/employee-admin-request.service';

/** @deprecated Login is handled by SSO. This service is kept for compatibility only. */
@Injectable({
  providedIn: 'root',
})
export class EmployeeLoginPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}
}
