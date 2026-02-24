import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'shared-basic-modal',
  standalone: true,
  templateUrl: './basic-modal.component.html',
  styleUrl: './basic-modal.component.scss',
})
export class BasicModalComponent {
  @Input() open = false;
  @Input() title = 'Модалка';
  @Input() text = '';

  @Output() closed = new EventEmitter<void>();
}
