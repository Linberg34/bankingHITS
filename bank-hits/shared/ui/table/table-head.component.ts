import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'shared-table-head',
  standalone: true,
  template: '<th [class.text-right]="align() === \'right\'"><ng-content /></th>',
  styles: [
    `
      :host-context(th) {
        font-weight: 600;
      }
      .text-right {
        text-align: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableHeadComponent {
  align = input<'left' | 'right'>('left');
}
