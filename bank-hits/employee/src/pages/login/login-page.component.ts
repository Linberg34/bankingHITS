import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { EmployeeLoginPageService } from './model';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.scss',
})
export class LoginPageComponent {
  email = '';
  isLoading = false;
  errorText = '';

  constructor(
    private readonly router: Router,
    private readonly employeeLoginPageService: EmployeeLoginPageService
  ) {}
  
  register(): void {
    const normalizedEmail = this.email.trim().toLowerCase();
    if (!normalizedEmail || this.isLoading) {
      return;
    }

    this.errorText = '';
    this.isLoading = true;

    this.employeeLoginPageService
      .login(normalizedEmail)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          void this.router.navigate(['/panel/accounts']);
        },
        error: (error: unknown) => {
          this.errorText = this.resolveErrorText(error);
        },
      });
  }

  private resolveErrorText(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Не удалось выполнить вход. Попробуйте позже.';
    }

    const backendMessage =
      typeof error.error === 'object' && error.error && 'message' in error.error
        ? String(error.error.message)
        : '';

    return backendMessage || 'Не удалось выполнить вход. Проверьте email и повторите попытку.';
  }
}
