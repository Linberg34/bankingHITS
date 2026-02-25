import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'shared-table-head',
  standalone: true,
  template: '<th [class.text-right]="align() === \'right\'"><ng-content /></th>',
  styleUrl: './table-head.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableHeadComponent {
  align = input<'left' | 'right'>('left');
}
