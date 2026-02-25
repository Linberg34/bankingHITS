import { ChangeDetectionStrategy, Component, model } from '@angular/core';

@Component({
  selector: 'shared-tabs',
  standalone: true,
  host: { class: 'shared-tabs' },
  template: '<ng-content />',
  styleUrl: './tabs.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TabsComponent {
  value = model<string>('');

  setValue(v: string): void {
    this.value.set(v);
  }
}
