import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-dialog-description',
  standalone: true,
  host: { class: 'shared-dialog-description' },
  template: '<ng-content />',
  styleUrl: './dialog-description.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DialogDescriptionComponent {}
