import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
  signal,
  effect,
} from '@angular/core';

@Component({
  selector: 'shared-dialog',
  standalone: true,
  templateUrl: './dialog.component.html',
  styleUrl: './dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DialogComponent {
  open = input<boolean>(false);
  openChange = output<boolean>();

  protected isOpen = signal(false);

  constructor() {
    effect(() => this.isOpen.set(this.open()));
  }

  protected close(): void {
    this.openChange.emit(false);
  }

  protected onOverlayClick(): void {
    this.close();
  }

  protected onContentClick(event: Event): void {
    event.stopPropagation();
  }
}
