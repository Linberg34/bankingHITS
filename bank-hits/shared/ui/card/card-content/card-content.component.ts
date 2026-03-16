import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-card-content',
  standalone: true,
  host: { class: 'shared-card-content' },
  template: '<ng-content />',
  styleUrl: './card-content.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CardContentComponent {}
