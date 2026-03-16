import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { EmployeeAdminRequestService } from '../../../app/infrastructure/request/employee-admin-request.service';

@Injectable({
  providedIn: 'root',
})
export class EmployeeLoginPageService {
  constructor(private readonly requestService: EmployeeAdminRequestService) {}

  login(email: string): Observable<void> {
    return this.requestService.login(email).pipe(map(() => void 0));
  }
}

