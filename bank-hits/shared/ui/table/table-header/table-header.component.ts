import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-table-header',
  standalone: true,
  template: '<thead><ng-content /></thead>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableHeaderComponent {}
