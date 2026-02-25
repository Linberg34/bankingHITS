import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-table-body',
  standalone: true,
  template: '<tbody><ng-content /></tbody>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableBodyComponent {}
