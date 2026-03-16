import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map, catchError, of } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthApiService } from '../../app/core/services/auth-api.service';
import { AuthRoleService } from '../../../../shared/auth';

export type LoginResult = { success: true } | { success: false; error: string };

const ERROR_MESSAGES: Record<number, string> = {
  401: 'Пользователь с таким email не найден.',
  403: 'Доступ запрещён.',
  404: 'Пользователь не найден.',
  500: 'Ошибка сервера. Попробуйте позже.',
  502: 'Сервис временно недоступен.',
  503: 'Сервис временно недоступен.',
};

@Injectable({ providedIn: 'root' })
export class ClientLoginPageService {
  private readonly authApi = inject(AuthApiService);
  private readonly authRole = inject(AuthRoleService);
  private readonly router = inject(Router);

  login(email: string): Observable<LoginResult> {
    return this.authApi.login(email).pipe(
      map(() => {
        this.authRole.setRole('client');
        void this.router.navigate(['/panel']);
        return { success: true as const };
      }),
      catchError((err: HttpErrorResponse) => {
        const message = this.getErrorMessage(err);
        return of({ success: false as const, error: message });
      })
    );
  }

  private getErrorMessage(err: HttpErrorResponse): string {
    if (err?.error?.message && typeof err.error.message === 'string') {
      return err.error.message;
    }
    const status = err?.status;
    if (status != null && ERROR_MESSAGES[status]) {
      return ERROR_MESSAGES[status];
    }
    if (status === 0 || err?.message === 'Http failure response for') {
      return 'Ошибка соединения. Проверьте сеть.';
    }
    return 'Не удалось войти. Попробуйте позже.';
  }
}
