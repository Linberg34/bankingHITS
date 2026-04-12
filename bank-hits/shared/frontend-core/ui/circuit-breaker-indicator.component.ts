import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FrontendCircuitBreakerService } from '../resilience/frontend-circuit-breaker.service';

@Component({
  selector: 'shared-circuit-breaker-indicator',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (snapshot().state !== 'CLOSED') {
      <div class="cb-indicator" [class]="'cb-indicator--' + snapshot().state.toLowerCase()">
        <span class="cb-indicator__dot"></span>
        <span class="cb-indicator__label">
          {{ stateLabel() }}
        </span>
      </div>
    }
  `,
  styles: [`
    .cb-indicator {
      align-items: center;
      border-radius: 20px;
      display: inline-flex;
      font-size: 13px;
      font-weight: 600;
      gap: 6px;
      padding: 4px 12px 4px 8px;
    }

    .cb-indicator--open {
      background: rgba(220, 38, 38, 0.12);
      color: #dc2626;
    }

    .cb-indicator--half_open {
      background: rgba(217, 119, 6, 0.12);
      color: #d97706;
    }

    .cb-indicator__dot {
      border-radius: 50%;
      display: inline-block;
      height: 8px;
      width: 8px;
    }

    .cb-indicator--open .cb-indicator__dot {
      background: #dc2626;
    }

    .cb-indicator--half_open .cb-indicator__dot {
      background: #d97706;
    }
  `],
})
export class CircuitBreakerIndicatorComponent {
  private readonly circuitBreaker = inject(FrontendCircuitBreakerService);

  readonly snapshot = this.circuitBreaker.snapshot;

  stateLabel(): string {
    const { state, errorRate } = this.snapshot();
    const pct = Math.round(errorRate * 100);
    switch (state) {
      case 'OPEN':
        return `Сервис недоступен (${pct}% ошибок)`;
      case 'HALF_OPEN':
        return 'Восстановление...';
      default:
        return '';
    }
  }
}
