import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-card-title',
  standalone: true,
  host: { class: 'shared-card-title' },
  template: '<ng-content />',
  styleUrl: './card-title.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CardTitleComponent {}
