import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, map, of, switchMap } from 'rxjs';
import { AuthRoleService } from 'shared/auth';
import { AppError, NotificationService, mapUnknownError } from 'shared/frontend-core';
import { ClientAuthRequestService } from '../../infrastructure/request/client-auth-request.service';

export type LoginResult = { success: true } | { success: false; error: string };

@Injectable({ providedIn: 'root' })
export class ClientSessionUseCasesService {
  private readonly request = inject(ClientAuthRequestService);
  private readonly authRoleService = inject(AuthRoleService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  login(email: string): Observable<LoginResult> {
    return this.request.login(email).pipe(
      switchMap(() => this.request.getCurrentUser()),
      map((user) => {
        this.authRoleService.setRole(user.role.toLowerCase() as 'client' | 'employee');
        void this.router.navigate(['/panel']);
        return { success: true } as const;
      }),
      catchError((error: unknown) => {
        const appError = mapUnknownError(error);
        const message = resolveErrorMessage(appError);
        this.notificationService.error(message);
        return of({ success: false as const, error: message });
      })
    );
  }

  getCurrentUser() {
    return this.request.getCurrentUser();
  }

  logout(): Observable<void> {
    return this.request.logout().pipe(
      map(() => {
        this.request.clearAuth();
        this.authRoleService.clearRole();
        return void 0;
      })
    );
  }

  clearSession(): void {
    this.request.clearAuth();
    this.authRoleService.clearRole();
  }
}

function resolveErrorMessage(error: AppError): string {
  if (error.kind === 'network') {
    return 'Network error. Check connection and retry.';
  }

  if (error.kind === 'auth') {
    return 'Authentication failed. Check email and try again.';
  }

  if (error.kind === 'validation') {
    return error.message;
  }

  return error.message || 'Action failed. Please retry.';
}
