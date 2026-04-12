import { Injectable, inject } from '@angular/core';
import { MONITORING_APP_NAME, type MonitoringApp } from './monitoring-app.token';
import { FrontendCircuitBreakerService, type CircuitState } from '../resilience/frontend-circuit-breaker.service';

type MonitoringService = 'users' | 'credits' | 'core' | 'sso';
type MonitoringLevel = 'info' | 'warn' | 'error';
type MonitoringMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';

interface MonitoringLogEntry {
  id: string;
  timestamp: number;
  app: MonitoringApp | 'system';
  service: MonitoringService;
  level: MonitoringLevel;
  method: MonitoringMethod;
  path: string;
  status: number;
  latencyMs: number;
  retries: number;
  blockedByCircuit: boolean;
  circuitState: CircuitState;
  traceId: string;
  message: string;
}

export interface MonitoringReportInput {
  method: string;
  url: string;
  status: number;
  latencyMs: number;
  retries: number;
  blockedByCircuit: boolean;
  traceId: string;
}

const MONITORING_API = 'http://sof-kov.ru:8087/api/monitoring/logs';

@Injectable({ providedIn: 'root' })
export class MonitoringReporterService {
  private readonly circuitBreaker = inject(FrontendCircuitBreakerService);
  private readonly appName: MonitoringApp | 'system' =
    inject(MONITORING_APP_NAME, { optional: true }) ?? 'system';

  report(input: MonitoringReportInput): void {
    const entry: MonitoringLogEntry = {
      id: createId(),
      timestamp: Date.now(),
      app: this.appName,
      service: detectService(input.url),
      level: detectLevel(input.status, input.blockedByCircuit, input.retries),
      method: (input.method.toUpperCase() as MonitoringMethod),
      path: extractPath(input.url),
      status: input.status,
      latencyMs: input.latencyMs,
      retries: input.retries,
      blockedByCircuit: input.blockedByCircuit,
      circuitState: this.circuitBreaker.snapshot().state,
      traceId: input.traceId,
      message: buildMessage(input),
    };

    // Use fetch directly to bypass Angular's HttpClient interceptor chain
    // and avoid circular dependency / infinite recursion
    fetch(MONITORING_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(entry),
    }).catch(() => undefined);
  }
}

function detectService(url: string): MonitoringService {
  if (url.includes('/credits') || url.includes('/tariffs')) return 'credits';
  if (
    url.includes('/users') ||
    url.includes('/clients') ||
    url.includes('/employees') ||
    url.includes('/settings') ||
    url.includes('/notifications')
  ) {
    return 'users';
  }
  if (url.includes('/sso') || url.includes('/auth') || url.includes('/login')) return 'sso';
  return 'core'; // accounts, operations, etc.
}

function detectLevel(
  status: number,
  blockedByCircuit: boolean,
  retries: number
): MonitoringLevel {
  if (blockedByCircuit || status >= 500) return 'error';
  if (retries > 0 || (status >= 400 && status < 500)) return 'warn';
  return 'info';
}

function extractPath(url: string): string {
  try {
    return new URL(url).pathname;
  } catch {
    return url.split('?')[0];
  }
}

function buildMessage(input: MonitoringReportInput): string {
  if (input.blockedByCircuit) {
    return `Circuit OPEN — request blocked: ${input.method} ${extractPath(input.url)}`;
  }
  if (input.status >= 500) {
    return `Server error ${input.status}: ${input.method} ${extractPath(input.url)} (${input.latencyMs}ms, retries: ${input.retries})`;
  }
  if (input.retries > 0) {
    return `Succeeded after ${input.retries} retries: ${input.method} ${extractPath(input.url)} (${input.latencyMs}ms)`;
  }
  return `${input.method} ${extractPath(input.url)} → ${input.status} (${input.latencyMs}ms)`;
}

function createId(): string {
  if (typeof globalThis.crypto?.randomUUID === 'function') {
    return globalThis.crypto.randomUUID();
  }
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
}
