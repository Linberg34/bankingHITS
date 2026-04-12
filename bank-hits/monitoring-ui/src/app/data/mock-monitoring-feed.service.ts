import { DestroyRef, Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { type MonitoringLogEntry } from '../models/monitoring-log.model';

const API_URL = '/api/monitoring/logs';
const POLL_MS = 2_000;

@Injectable({ providedIn: 'root' })
export class MonitoringFeedService {
  private readonly http = inject(HttpClient);

  readonly logs = signal<MonitoringLogEntry[]>([]);
  readonly paused = signal(false);

  constructor(destroyRef: DestroyRef) {
    this.refresh();

    const timerId = setInterval(() => {
      if (!this.paused()) {
        this.refresh();
      }
    }, POLL_MS);

    destroyRef.onDestroy(() => clearInterval(timerId));
  }

  togglePause(): void {
    this.paused.update((current) => !current);

    if (!this.paused()) {
      this.refresh();
    }
  }

  clear(): void {
    this.http.delete<void>(API_URL).subscribe({
      next: () => this.logs.set([]),
      error: (error) => console.error('Failed to clear monitoring logs', error),
    });
  }

  burst(): void {
    this.refresh();
  }

  private refresh(): void {
    this.http.get<MonitoringLogEntry[]>(API_URL).subscribe({
      next: (entries) =>
        this.logs.set(
          [...entries].sort((left, right) => right.timestamp - left.timestamp)
        ),
      error: (error) => console.error('Failed to load monitoring logs', error),
    });
  }
}
