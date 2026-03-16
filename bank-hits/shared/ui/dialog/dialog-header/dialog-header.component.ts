import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-dialog-header',
  standalone: true,
  host: { class: 'shared-dialog-header' },
  template: '<ng-content />',
  styleUrl: './dialog-header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DialogHeaderComponent {}
