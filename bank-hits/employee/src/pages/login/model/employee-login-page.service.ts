import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AuthApiService } from '../../../../../shared/entities/auth';

@Injectable({
  providedIn: 'root',
})
export class EmployeeLoginPageService {
  constructor(private readonly authApiService: AuthApiService) {}

  login(email: string): Observable<void> {
    return this.authApiService
      .login({
        email,
      })
      .pipe(map(() => void 0));
  }
}
