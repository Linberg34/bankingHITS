import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
  output,
} from '@angular/core';

export interface SelectOption {
  value: string;
  label: string;
}

@Component({
  selector: 'shared-select',
  standalone: true,
  host: { class: 'shared-select-host' },
  template: `
    <select
      class="shared-select"
      [value]="displayValue()"
      [disabled]="disabled()"
      (change)="onChange($event)"
    >
      @if (placeholder()) {
        <option value="">{{ placeholder() }}</option>
      }
      @for (opt of options(); track opt.value) {
        <option [value]="opt.value">{{ opt.label }}</option>
      }
    </select>
  `,
  styleUrl: './select.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectComponent {
  options = input<SelectOption[]>([]);
  placeholder = input<string>('');
  disabled = input<boolean>(false);
  value = input<string>('');

  valueChange = output<string>();

  protected displayValue = computed(() => this.value() ?? '');

  onChange(event: Event): void {
    const v = (event.target as HTMLSelectElement).value;
    this.valueChange.emit(v);
  }
}
