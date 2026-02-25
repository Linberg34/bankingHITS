import { ChangeDetectionStrategy, Component, input, inject } from '@angular/core';
import { TabsComponent } from '../tabs/tabs.component';

@Component({
  selector: 'shared-tabs-content',
  standalone: true,
  host: {
    class: 'shared-tabs-content',
    '[attr.hidden]': 'isHidden() ? true : null',
  },
  template: '<ng-content />',
  styleUrl: './tabs-content.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TabsContentComponent {
  value = input.required<string>();
  private readonly tabs = inject(TabsComponent, { optional: true });

  isHidden(): boolean {
    const tabs = this.tabs;
    if (!tabs) return false;
    return tabs.value() !== this.value();
  }
}
