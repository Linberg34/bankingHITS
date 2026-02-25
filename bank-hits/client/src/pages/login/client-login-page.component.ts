import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-client-login-page',
  standalone: true,
  templateUrl: './client-login-page.component.html',
  styleUrl: './client-login-page.component.scss',
})
export class ClientLoginPageComponent {
  private readonly router = inject(Router);

  register(): void {
    void this.router.navigate(['/dashboard']);
  }
}
