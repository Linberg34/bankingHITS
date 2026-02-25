import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-card-description',
  standalone: true,
  host: { class: 'shared-card-description' },
  template: '<ng-content />',
  styleUrl: './card-description.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CardDescriptionComponent {}
