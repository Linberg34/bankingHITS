import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-dialog-title',
  standalone: true,
  host: { class: 'shared-dialog-title' },
  template: '<ng-content />',
  styleUrl: './dialog-title.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DialogTitleComponent {}
