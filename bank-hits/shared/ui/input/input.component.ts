import { ChangeDetectionStrategy, Component, input, output, model } from '@angular/core';

@Component({
  selector: 'shared-input',
  standalone: true,
  host: { class: 'shared-input-host' },
  template: `
    <input
      class="shared-input"
      [type]="type()"
      [placeholder]="placeholder()"
      [disabled]="disabled()"
      [value]="value()"
      (input)="onInput($event)"
    />
  `,
  styleUrl: './input.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InputComponent {
  type = input<string>('text');
  placeholder = input<string>('');
  disabled = input<boolean>(false);
  value = model<string>('');

  valueChange = output<string>();

  onInput(event: Event): void {
    const v = (event.target as HTMLInputElement).value;
    this.value.set(v);
    this.valueChange.emit(v);
  }
}
