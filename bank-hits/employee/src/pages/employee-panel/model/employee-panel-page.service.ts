import { Injectable } from '@angular/core';
import { Observable, finalize, map } from 'rxjs';
import { EmployeeAdminRequestService } from '../../../app/infrastructure/request/employee-admin-request.service';

@Injectable({
  providedIn: 'root',
})
export class EmployeePanelPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  logout(): Observable<void> {
    return this.requestService.logout().pipe(
      finalize(() => this.requestService.clearAuth()),
      map(() => void 0)
    );
  }
}

