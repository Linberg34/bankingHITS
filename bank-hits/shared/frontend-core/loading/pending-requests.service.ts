import { Injectable, computed, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PendingRequestsService {
  private readonly pendingCount = signal(0);

  readonly isPending = computed(() => this.pendingCount() > 0);

  start(): void {
    this.pendingCount.update((value) => value + 1);
  }

  finish(): void {
    this.pendingCount.update((value) => (value > 0 ? value - 1 : 0));
  }
}
