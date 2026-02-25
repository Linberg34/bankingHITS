import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'shared-badge',
  standalone: true,
  host: {
    class: 'shared-badge',
    '[class.shared-badge--default]': 'variant() === "default"',
    '[class.shared-badge--secondary]': 'variant() === "secondary"',
    '[class.shared-badge--destructive]': 'variant() === "destructive"',
  },
  template: '<ng-content />',
  styleUrl: './badge.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BadgeComponent {
  variant = input<'default' | 'secondary' | 'destructive'>('default');
}
