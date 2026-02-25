import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-card-header',
  standalone: true,
  host: { class: 'shared-card-header' },
  template: '<ng-content />',
  styleUrl: './card-header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CardHeaderComponent {}
