import { Injectable, inject } from '@angular/core';
import { Observable, finalize, map } from 'rxjs';
import { EmployeeAdminApiService } from '../../data-access/api/employee-admin-api.service';

@Injectable({ providedIn: 'root' })
export class EmployeeSessionUseCasesService {
  private readonly api = inject(EmployeeAdminApiService);

  login(email: string): Observable<void> {
    return this.api.login(email).pipe(map(() => void 0));
  }

  logout(): Observable<void> {
    return this.api.logout().pipe(
      finalize(() => this.api.clearAuth()),
      map(() => void 0)
    );
  }
}

