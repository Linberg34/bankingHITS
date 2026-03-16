import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import {
  DialogComponent,
  DialogDescriptionComponent,
  DialogFooterComponent,
  DialogHeaderComponent,
  DialogTitleComponent,
} from '../dialog';
import { ButtonComponent } from '../button';

@Component({
  selector: 'shared-confirm-dialog',
  standalone: true,
  imports: [
    DialogComponent,
    DialogHeaderComponent,
    DialogTitleComponent,
    DialogDescriptionComponent,
    DialogFooterComponent,
    ButtonComponent,
  ],
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmDialogComponent {
  open = input<boolean>(false);
  title = input<string>('Confirm action');
  description = input<string>('Do you want to continue?');
  confirmText = input<string>('Confirm');
  cancelText = input<string>('Cancel');
  confirmDisabled = input<boolean>(false);

  openChange = output<boolean>();
  confirmed = output<void>();

  protected onConfirm(): void {
    this.confirmed.emit();
  }
}

