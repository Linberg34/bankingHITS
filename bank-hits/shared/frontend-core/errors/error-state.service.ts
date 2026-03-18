import { Injectable, signal } from '@angular/core';
import { AppError } from './app-error';

@Injectable({ providedIn: 'root' })
export class ErrorStateService {
  readonly lastUnhandledError = signal<AppError | null>(null);

  setUnhandledError(error: AppError): void {
    this.lastUnhandledError.set(error);
  }

  clear(): void {
    this.lastUnhandledError.set(null);
  }
}

