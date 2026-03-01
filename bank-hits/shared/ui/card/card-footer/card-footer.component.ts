import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-card-footer',
  standalone: true,
  host: { class: 'shared-card-footer' },
  template: '<ng-content />',
  styleUrl: './card-footer.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CardFooterComponent {}
