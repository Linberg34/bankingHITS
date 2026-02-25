import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-table-row',
  standalone: true,
  template: '<tr><ng-content /></tr>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableRowComponent {}
