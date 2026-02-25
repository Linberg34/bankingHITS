import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'shared-table',
  standalone: true,
  host: { class: 'shared-table' },
  template: '<table class="shared-table__inner"><ng-content /></table>',
  styleUrl: './table.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableComponent {}
