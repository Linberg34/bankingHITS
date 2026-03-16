import { ErrorHandler, Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { mapUnknownError } from './error-mapper';
import { ErrorStateService } from './error-state.service';

@Injectable()
export class GlobalAppErrorHandler implements ErrorHandler {
  private readonly router = inject(Router);
  private readonly errorState = inject(ErrorStateService);

  handleError(error: unknown): void {
    const mapped = mapUnknownError(error);
    this.errorState.setUnhandledError(mapped);
    void this.router.navigate(['/error']);
    console.error(error);
  }
}

