import { Injectable } from '@angular/core';
import { Observable, finalize, map } from 'rxjs';
import { AuthApiService } from '../../../../../shared/entities/auth';

@Injectable({
  providedIn: 'root',
})
export class EmployeePanelPageService {
  constructor(private readonly authApiService: AuthApiService) {}

  logout(): Observable<void> {
    return this.authApiService.logout().pipe(
      finalize(() => this.authApiService.clearAuth()),
      map(() => void 0)
    );
  }
}
