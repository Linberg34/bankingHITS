import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { NotificationService } from '../notifications/notification.service';

@Component({
  selector: 'shared-notification-center',
  standalone: true,
  templateUrl: './notification-center.component.html',
  styleUrl: './notification-center.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationCenterComponent {
  protected readonly notificationService = inject(NotificationService);
}

