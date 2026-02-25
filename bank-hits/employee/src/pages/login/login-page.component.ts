import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthRoleService } from '../../../../shared/auth';

@Component({
  selector: 'app-login-page',
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
    this.authRoleService.setRole('employee');
    void this.router.navigate(['/panel/accounts']);
  }
}
