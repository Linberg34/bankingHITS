import { HttpErrorResponse, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, retry, tap, throwError, timer } from 'rxjs';
import { FrontendRequestMonitoringService } from '../monitoring/frontend-request-monitoring.service';
import { FrontendCircuitBreakerService } from './frontend-circuit-breaker.service';

const MAX_RETRIES = 2;
const TRACE_HEADER = 'X-Trace-Id';
const RETRYABLE_METHODS = new Set(['GET', 'HEAD', 'OPTIONS', 'POST', 'PUT', 'PATCH', 'DELETE']);

export const resilienceInterceptor: HttpInterceptorFn = (request, next) => {
  const circuitBreaker = inject(FrontendCircuitBreakerService);
  const monitoring = inject(FrontendRequestMonitoringService);

  const traceId = request.headers.get(TRACE_HEADER) ?? createTraceId();
  const tracedRequest = request.headers.has(TRACE_HEADER)
    ? request
    : request.clone({ setHeaders: { [TRACE_HEADER]: traceId } });

  const start = Date.now();
  const decision = circuitBreaker.beforeRequest(start);
  if (!decision.allow) {
    monitoring.recordCircuitBlocked();
    console.warn(`[trace] blocked ${tracedRequest.method} ${tracedRequest.urlWithParams} status=503 traceId=${traceId}`);
    return throwError(() => createCircuitOpenError());
  }

  monitoring.recordStarted();
  let retriesUsed = 0;

  return next(tracedRequest).pipe(
    retry({
      count: MAX_RETRIES,
      delay: (error, retryCount) => {
        if (!isRetryable(error, tracedRequest.method)) {
          throw error;
        }

        retriesUsed = retryCount;
        monitoring.recordRetry();
        return timer(backoffDelayMs(retryCount));
      },
    }),
    tap((event) => {
      if (event instanceof HttpResponse) {
        const latency = Date.now() - start;
        circuitBreaker.onRequestSuccess(decision);
        monitoring.recordSuccess(latency);
        console.info(
          `[trace] ok ${tracedRequest.method} ${tracedRequest.urlWithParams} status=${event.status} latencyMs=${latency} traceId=${traceId}`
        );
        if (retriesUsed > 0) {
          console.warn(
            `[retry] ${tracedRequest.method} ${tracedRequest.urlWithParams} succeeded after ${retriesUsed} retries traceId=${traceId}`
          );
        }
      }
    }),
    catchError((error) => {
      const latency = Date.now() - start;
      circuitBreaker.onRequestFailure(decision);
      monitoring.recordFailure(latency);
      const status = error instanceof HttpErrorResponse ? error.status : 'unknown';
      console.error(
        `[trace] failed ${tracedRequest.method} ${tracedRequest.urlWithParams} status=${status} latencyMs=${latency} traceId=${traceId}`
      );
      return throwError(() => error);
    })
  );
};

function isRetryable(error: unknown, method: string): boolean {
  if (!RETRYABLE_METHODS.has(method.toUpperCase())) {
    return false;
  }

  if (!(error instanceof HttpErrorResponse)) {
    return false;
  }

  return error.status === 0 || error.status >= 500;
}

function backoffDelayMs(retryCount: number): number {
  const base = 250 * Math.pow(2, Math.max(0, retryCount - 1));
  const jitter = Math.floor(Math.random() * 150);
  return base + jitter;
}

function createCircuitOpenError(): HttpErrorResponse {
  return new HttpErrorResponse({
    status: 503,
    statusText: 'Circuit Open',
    error: {
      code: 'CIRCUIT_OPEN',
      message: 'Requests are temporarily blocked due to high error rate.',
    },
  });
}

function createTraceId(): string {
  if (typeof globalThis.crypto?.randomUUID === 'function') {
    return globalThis.crypto.randomUUID();
  }

  return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
}
