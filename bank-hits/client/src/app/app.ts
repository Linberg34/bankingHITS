import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { onMessage } from 'firebase/messaging';
import {
  messaging,
  NotificationCenterComponent,
  NotificationService,
  ThemeModeService,
} from '../../../shared/frontend-core';

@Component({
  imports: [RouterModule, NotificationCenterComponent],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  constructor(
    private readonly themeModeService: ThemeModeService,
    private readonly notifications: NotificationService
  ) {
    void this.themeModeService.mode;
  }

  ngOnInit(): void {
    onMessage(messaging, (payload) => {
      const title = payload.notification?.title ?? 'Уведомление';
      const body = payload.notification?.body ?? '';
      if (document.visibilityState === 'hidden' && Notification.permission === 'granted') {
        navigator.serviceWorker.ready.then((reg) => reg.showNotification(title, { body }));
      } else {
        this.notifications.info(`${title}: ${body}`);
      }
    });
  }
}
