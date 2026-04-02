import { HttpInterceptorFn } from '@angular/common/http';

const IDEMPOTENCY_HEADER = 'Idempotency-Key';
const IDEMPOTENT_METHODS = new Set(['POST', 'PUT', 'PATCH', 'DELETE']);

export const idempotencyKeyInterceptor: HttpInterceptorFn = (request, next) => {
  if (!IDEMPOTENT_METHODS.has(request.method)) {
    return next(request);
  }

  if (request.headers.has(IDEMPOTENCY_HEADER)) {
    return next(request);
  }

  return next(
    request.clone({
      setHeaders: {
        [IDEMPOTENCY_HEADER]: createIdempotencyKey(),
      },
    })
  );
};

function createIdempotencyKey(): string {
  if (typeof globalThis.crypto?.randomUUID === 'function') {
    return globalThis.crypto.randomUUID();
  }

  return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
}
