import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { PendingRequestsService } from '../loading/pending-requests.service';

@Component({
  selector: 'shared-pending-request-indicator',
  standalone: true,
  templateUrl: './pending-request-indicator.component.html',
  styleUrl: './pending-request-indicator.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PendingRequestIndicatorComponent {
  protected readonly pendingRequests = inject(PendingRequestsService);
}
