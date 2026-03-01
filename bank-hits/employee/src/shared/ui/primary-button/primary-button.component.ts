import { Component, Input } from '@angular/core';

@Component({
  selector: 'shared-primary-button',
  standalone: true,
  templateUrl: './primary-button.component.html',
  styleUrl: './primary-button.component.scss',
})
export class PrimaryButtonComponent {
  @Input() label = 'Button';
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
}
