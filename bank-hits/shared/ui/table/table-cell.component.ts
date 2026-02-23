import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'shared-table-cell',
  standalone: true,
  template: '<td [class.text-right]="align() === \'right\'"><ng-content /></td>',
  styles: [
    `
      .text-right {
        text-align: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableCellComponent {
  align = input<'left' | 'right'>('left');
}
