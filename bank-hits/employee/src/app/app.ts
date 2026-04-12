import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { onMessage } from 'firebase/messaging';
import {
  messaging,
  NotificationCenterComponent,
  NotificationService,
  ThemeModeService,
} from '../../../shared/frontend-core';

@Component({
  imports: [RouterOutlet, NotificationCenterComponent],
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
      this.notifications.info(`${title}: ${body}`);
    });
  }
}
