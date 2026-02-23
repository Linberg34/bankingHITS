import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'shared-label',
  standalone: true,
  host: {
    class: 'shared-label',
    '[attr.for]': 'for()',
  },
  template: '<ng-content />',
  styleUrl: './label.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LabelComponent {
  for = input<string | null>(null);
}
