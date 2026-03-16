export type AppErrorKind =
  | 'network'
  | 'auth'
  | 'validation'
  | 'domain'
  | 'not-found'
  | 'unknown';

export class AppError extends Error {
  constructor(
    public readonly kind: AppErrorKind,
    message: string,
    public readonly statusCode?: number,
    public readonly details?: unknown
  ) {
    super(message);
    this.name = 'AppError';
  }
}

export function isAppError(value: unknown): value is AppError {
  return value instanceof AppError;
}

