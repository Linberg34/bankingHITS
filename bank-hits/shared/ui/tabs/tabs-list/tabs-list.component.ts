import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-tabs-list',
  standalone: true,
  host: { class: 'shared-tabs-list' },
  template: '<ng-content />',
  styleUrl: './tabs-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TabsListComponent {}
