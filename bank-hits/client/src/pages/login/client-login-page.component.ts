import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthRoleService } from '../../../../shared/auth';

@Component({
  selector: 'app-client-login-page',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './client-login-page.component.html',
  styleUrl: './client-login-page.component.scss',
})
export class ClientLoginPageComponent {
  private readonly router = inject(Router);
  private readonly authRoleService = inject(AuthRoleService);

  protected email = signal('');

  submit(): void {
    this.authRoleService.setRole('client');
    void this.router.navigate(['/panel']);
  }
}
