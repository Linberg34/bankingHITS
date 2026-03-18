import { HttpErrorResponse } from '@angular/common/http';
import { AppError } from './app-error';

const DEFAULT_ERROR_MESSAGE = 'Something went wrong. Please try again.';

export function mapHttpError(error: HttpErrorResponse): AppError {
  const backendMessage =
    typeof error.error === 'object' && error.error && 'message' in error.error
      ? String(error.error.message)
      : '';

  const message = backendMessage || error.message || DEFAULT_ERROR_MESSAGE;

  if (error.status === 401 || error.status === 403) {
    return new AppError('auth', message, error.status, error.error);
  }

  if (error.status === 404) {
    return new AppError('not-found', message, error.status, error.error);
  }

  if (error.status === 400 || error.status === 422) {
    return new AppError('validation', message, error.status, error.error);
  }

  if (error.status >= 500 || error.status === 0) {
    return new AppError('network', message, error.status, error.error);
  }

  return new AppError('unknown', message, error.status, error.error);
}

export function mapUnknownError(error: unknown): AppError {
  if (error instanceof AppError) {
    return error;
  }

  if (error instanceof HttpErrorResponse) {
    return mapHttpError(error);
  }

  if (error instanceof Error) {
    return new AppError('unknown', error.message, undefined, error);
  }

  return new AppError('unknown', DEFAULT_ERROR_MESSAGE, undefined, error);
}

