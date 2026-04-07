import { Injectable, signal } from '@angular/core';

export interface FrontendRequestMetrics {
  started: number;
  succeeded: number;
  failed: number;
  retries: number;
  blockedByCircuit: number;
  lastLatencyMs: number | null;
  errorRate: number;
}

const ERROR_RATE_WINDOW_MS = 60_000;

@Injectable({ providedIn: 'root' })
export class FrontendRequestMonitoringService {
  readonly metrics = signal<FrontendRequestMetrics>({
    started: 0,
    succeeded: 0,
    failed: 0,
    retries: 0,
    blockedByCircuit: 0,
    lastLatencyMs: null,
    errorRate: 0,
  });
  private readonly outcomes: Array<{ ts: number; success: boolean }> = [];

  recordStarted(): void {
    this.metrics.update((current) => ({ ...current, started: current.started + 1 }));
  }

  recordSuccess(latencyMs: number): void {
    this.recordOutcome(true);
    this.metrics.update((current) => ({
      ...current,
      succeeded: current.succeeded + 1,
      lastLatencyMs: latencyMs,
      errorRate: this.computeErrorRate(),
    }));
  }

  recordFailure(latencyMs: number): void {
    this.recordOutcome(false);
    this.metrics.update((current) => ({
      ...current,
      failed: current.failed + 1,
      lastLatencyMs: latencyMs,
      errorRate: this.computeErrorRate(),
    }));
  }

  recordRetry(): void {
    this.metrics.update((current) => ({ ...current, retries: current.retries + 1 }));
  }

  recordCircuitBlocked(): void {
    this.metrics.update((current) => ({
      ...current,
      blockedByCircuit: current.blockedByCircuit + 1,
    }));
  }

  private recordOutcome(success: boolean): void {
    const now = Date.now();
    this.outcomes.push({ ts: now, success });
    this.prune(now);
  }

  private prune(now: number): void {
    const minTs = now - ERROR_RATE_WINDOW_MS;
    while (this.outcomes.length && this.outcomes[0].ts < minTs) {
      this.outcomes.shift();
    }
  }

  private computeErrorRate(): number {
    const total = this.outcomes.length;
    if (!total) {
      return 0;
    }

    const failed = this.outcomes.reduce((acc, outcome) => (outcome.success ? acc : acc + 1), 0);
    return failed / total;
  }
}
