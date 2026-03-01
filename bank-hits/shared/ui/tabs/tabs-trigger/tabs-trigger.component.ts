import { ChangeDetectionStrategy, Component, input, inject } from '@angular/core';
import { TabsComponent } from '../tabs/tabs.component';

@Component({
  selector: 'shared-tabs-trigger',
  standalone: true,
  host: {
    class: 'shared-tabs-trigger',
    role: 'tab',
    '(click)': 'onClick()',
  },
  template: '<ng-content />',
  styleUrl: './tabs-trigger.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TabsTriggerComponent {
  value = input.required<string>();
  private readonly tabs = inject(TabsComponent, { optional: true });

  onClick(): void {
    this.tabs?.setValue(this.value());
  }
}
