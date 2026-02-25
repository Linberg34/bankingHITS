import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-dialog-footer',
  standalone: true,
  host: { class: 'shared-dialog-footer' },
  template: '<ng-content />',
  styleUrl: './dialog-footer.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DialogFooterComponent {}
