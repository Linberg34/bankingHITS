import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-card',
  standalone: true,
  host: { class: 'shared-card' },
  template: '<ng-content />',
  styleUrl: './card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CardComponent {}
