import { Injectable, signal } from '@angular/core';

export type NotificationLevel = 'success' | 'error' | 'info';

export interface AppNotification {
  id: number;
  level: NotificationLevel;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly notifications = signal<AppNotification[]>([]);
  private idCounter = 0;

  success(message: string): void {
    this.push('success', message);
  }

  error(message: string): void {
    this.push('error', message);
  }

  info(message: string): void {
    this.push('info', message);
  }

  dismiss(id: number): void {
    this.notifications.update((current) => current.filter((item) => item.id !== id));
  }

  private push(level: NotificationLevel, message: string): void {
    const id = ++this.idCounter;
    this.notifications.update((current) => [...current, { id, level, message }]);
    setTimeout(() => this.dismiss(id), 4000);
  }
}

