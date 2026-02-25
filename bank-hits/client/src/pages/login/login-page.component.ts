import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthRoleService } from '../../../../shared/auth';

@Component({
  selector: 'client-login-page',
  standalone: true,
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.scss',
})
export class LoginPageComponent {
  constructor(
    private readonly router: Router,
    private readonly authRoleService: AuthRoleService
  ) {}

  register(): void {
    this.authRoleService.setRole('client');
    void this.router.navigate(['/panel']);
  }
}
