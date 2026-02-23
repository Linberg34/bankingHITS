import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'shared-button',
  standalone: true,
  host: {
    class: 'shared-button',
    '[class.shared-button--default]': 'variant() === "default"',
    '[class.shared-button--outline]': 'variant() === "outline"',
    '[class.shared-button--ghost]': 'variant() === "ghost"',
    '[class.shared-button--sm]': 'size() === "sm"',
    '[class.shared-button--lg]': 'size() === "lg"',
    type: 'button',
    '[attr.disabled]': 'disabled() ? true : null',
  },
  template: '<ng-content />',
  styleUrl: './button.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ButtonComponent {
  variant = input<'default' | 'outline' | 'ghost'>('default');
  size = input<'default' | 'sm' | 'lg'>('default');
  disabled = input<boolean>(false);
}
