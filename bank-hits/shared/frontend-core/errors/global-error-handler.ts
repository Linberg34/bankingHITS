import { ErrorHandler, Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { mapUnknownError } from './error-mapper';
import { ErrorStateService } from './error-state.service';
import { NotificationService } from '../notifications/notification.service';

@Injectable()
export class GlobalAppErrorHandler implements ErrorHandler {
  private readonly router = inject(Router);
  private readonly errorState = inject(ErrorStateService);
  private readonly notifications = inject(NotificationService);

  handleError(error: unknown): void {
    const mapped = mapUnknownError(error);
    console.error(error);

    // Transient network / server errors (5xx, status=0, circuit-open 503):
    // do NOT crash the whole app — just show a toast so the user can retry.
    if (mapped.kind === 'network') {
      this.notifications.error('Сервис временно недоступен. Попробуйте повторить запрос.');
      return;
    }

    // Permanent or unknown errors navigate to the error page.
    this.errorState.setUnhandledError(mapped);
    void this.router.navigate(['/error']);
  }
}

