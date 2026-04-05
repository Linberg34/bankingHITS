import { DestroyRef, Injectable, signal } from '@angular/core';
import {
  type CircuitState,
  type MonitoringApp,
  type MonitoringLevel,
  type MonitoringLogEntry,
  type MonitoringService,
} from '../models/monitoring-log.model';

const MAX_LOGS = 600;
const TICK_MS = 1300;

const APPS: MonitoringApp[] = ['client', 'employee'];
const SERVICES: MonitoringService[] = ['users', 'credits', 'core'];
const METHODS: MonitoringLogEntry['method'][] = ['GET', 'POST', 'PUT', 'PATCH', 'DELETE'];

@Injectable({ providedIn: 'root' })
export class MockMonitoringFeedService {
  readonly logs = signal<MonitoringLogEntry[]>(createSeedLogs());
  readonly paused = signal(false);

  constructor(destroyRef: DestroyRef) {
    const timerId = setInterval(() => {
      if (this.paused()) {
        return;
      }

      this.push(generateLog(Date.now()));
    }, TICK_MS);

    destroyRef.onDestroy(() => clearInterval(timerId));
  }

  togglePause(): void {
    this.paused.update((current) => !current);
  }

  clear(): void {
    this.logs.set([]);
  }

  burst(size = 30): void {
    const now = Date.now();
    const entries: MonitoringLogEntry[] = [];
    for (let index = 0; index < size; index++) {
      entries.push(generateLog(now - index * 300));
    }

    this.logs.update((current) => trimLogs([...entries.reverse(), ...current]));
  }

  private push(entry: MonitoringLogEntry): void {
    this.logs.update((current) => trimLogs([entry, ...current]));
  }
}

function trimLogs(logs: MonitoringLogEntry[]): MonitoringLogEntry[] {
  return logs.slice(0, MAX_LOGS);
}

function createSeedLogs(): MonitoringLogEntry[] {
  const now = Date.now();
  const entries: MonitoringLogEntry[] = [];
  for (let index = 0; index < 120; index++) {
    entries.push(generateLog(now - index * 2_000));
  }

  return entries.sort((left, right) => right.timestamp - left.timestamp);
}

function generateLog(ts: number): MonitoringLogEntry {
  const app = pick(APPS);
  const service = pick(SERVICES);
  const method = pick(METHODS);
  const path = pickPath(service, method);

  const minute = new Date(ts).getMinutes();
  const failureChance = minute % 2 === 0 ? 0.7 : 0.3;
  const blockedByCircuit = Math.random() < 0.06;

  if (blockedByCircuit) {
    return {
      id: createId(),
      timestamp: ts,
      app,
      service,
      level: 'error',
      method,
      path,
      status: 503,
      latencyMs: rand(6, 20),
      retries: 0,
      blockedByCircuit: true,
      circuitState: 'OPEN',
      traceId: createId(),
      message: 'Запрос заблокирован circuit breaker',
    };
  }

  const failed = Math.random() < failureChance;
  const retries = failed ? rand(0, 2) : rand(0, 1);
  const status = failed ? 500 : pick([200, 201, 204]);
  const latencyMs = failed ? rand(850, 2600) : rand(65, 820);

  return {
    id: createId(),
    timestamp: ts,
    app,
    service,
    level: failed ? 'error' : retries > 0 ? 'warn' : 'info',
    method,
    path,
    status,
    latencyMs,
    retries,
    blockedByCircuit: false,
    circuitState: failed && retries > 1 ? 'HALF_OPEN' : 'CLOSED',
    traceId: createId(),
    message: failed ? 'Обнаружена нестабильность upstream-сервиса' : 'Запрос выполнен',
  };
}

function pickPath(service: MonitoringService, method: MonitoringLogEntry['method']): string {
  switch (service) {
    case 'users':
      return method === 'GET' ? '/api/users/profile' : '/api/users';
    case 'credits':
      return method === 'GET' ? '/api/credits' : '/api/credits/applications';
    default:
      return method === 'GET' ? '/api/core/health' : '/api/core/events';
  }
}

function pick<T>(items: readonly T[]): T {
  return items[Math.floor(Math.random() * items.length)];
}

function rand(min: number, max: number): number {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function createId(): string {
  if (typeof globalThis.crypto?.randomUUID === 'function') {
    return globalThis.crypto.randomUUID();
  }

  return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
}
