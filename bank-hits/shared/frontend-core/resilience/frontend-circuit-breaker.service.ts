import { Injectable, signal } from '@angular/core';

export type CircuitState = 'CLOSED' | 'OPEN' | 'HALF_OPEN';

export interface CircuitSnapshot {
  state: CircuitState;
  errorRate: number;
  total: number;
  failed: number;
  openUntil: number | null;
}

export interface CircuitDecision {
  allow: boolean;
  probe: boolean;
}

const WINDOW_MS = 60_000;
const OPEN_STATE_MS = 15_000;
const MIN_REQUESTS_TO_EVALUATE = 10;
const OPEN_ERROR_RATE_THRESHOLD = 0.7;

@Injectable({ providedIn: 'root' })
export class FrontendCircuitBreakerService {
  readonly snapshot = signal<CircuitSnapshot>({
    state: 'CLOSED',
    errorRate: 0,
    total: 0,
    failed: 0,
    openUntil: null,
  });

  private readonly outcomes: Array<{ ts: number; success: boolean }> = [];
  private probeInFlight = false;
  private openUntil: number | null = null;

  beforeRequest(now = Date.now()): CircuitDecision {
    this.prune(now);

    const current = this.snapshot();
    if (current.state === 'OPEN') {
      if (this.openUntil !== null && now >= this.openUntil) {
        this.setState('HALF_OPEN');
      } else {
        return { allow: false, probe: false };
      }
    }

    if (this.snapshot().state === 'HALF_OPEN') {
      if (this.probeInFlight) {
        return { allow: false, probe: false };
      }
      this.probeInFlight = true;
      return { allow: true, probe: true };
    }

    return { allow: true, probe: false };
  }

  onRequestSuccess(decision: CircuitDecision, now = Date.now()): void {
    this.recordOutcome(true, now);

    if (decision.probe && this.snapshot().state === 'HALF_OPEN') {
      this.probeInFlight = false;
      this.openUntil = null;
      this.setState('CLOSED');
      return;
    }

    this.refreshSnapshot();
  }

  onRequestFailure(decision: CircuitDecision, now = Date.now()): void {
    this.recordOutcome(false, now);

    if (decision.probe && this.snapshot().state === 'HALF_OPEN') {
      this.probeInFlight = false;
      this.toOpen(now);
      return;
    }

    this.evaluateAndMaybeOpen(now);
  }

  private evaluateAndMaybeOpen(now: number): void {
    this.prune(now);
    const { total, failed, errorRate } = this.computeMetrics();
    if (total >= MIN_REQUESTS_TO_EVALUATE && errorRate > OPEN_ERROR_RATE_THRESHOLD) {
      this.toOpen(now);
      return;
    }
    this.refreshSnapshot();
  }

  private toOpen(now: number): void {
    this.openUntil = now + OPEN_STATE_MS;
    this.probeInFlight = false;
    this.setState('OPEN');
    this.refreshSnapshot();
  }

  private setState(state: CircuitState): void {
    const current = this.snapshot();
    this.snapshot.set({
      ...current,
      state,
      openUntil: state === 'OPEN' ? this.openUntil : null,
    });
  }

  private recordOutcome(success: boolean, now: number): void {
    this.outcomes.push({ ts: now, success });
    this.prune(now);
  }

  private prune(now: number): void {
    const minTs = now - WINDOW_MS;
    while (this.outcomes.length && this.outcomes[0].ts < minTs) {
      this.outcomes.shift();
    }
  }

  private refreshSnapshot(): void {
    const current = this.snapshot();
    const { total, failed, errorRate } = this.computeMetrics();
    this.snapshot.set({
      ...current,
      total,
      failed,
      errorRate,
      openUntil: current.state === 'OPEN' ? this.openUntil : null,
    });
  }

  private computeMetrics(): { total: number; failed: number; errorRate: number } {
    const total = this.outcomes.length;
    const failed = this.outcomes.reduce((acc, item) => (item.success ? acc : acc + 1), 0);
    const errorRate = total ? failed / total : 0;
    return { total, failed, errorRate };
  }
}
