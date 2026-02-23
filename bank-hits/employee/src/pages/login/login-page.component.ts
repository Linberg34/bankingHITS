import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'employee-login-page',
  standalone: true,
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.scss',
})
export class LoginPageComponent {
  constructor(private readonly router: Router) {}

  register(): void {
    void this.router.navigate(['/panel']);
  }
}
