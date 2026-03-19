import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { NotificationService, ThemeModeService } from '../../../../shared/frontend-core';
import { HeaderComponent } from '../../../../shared/ui/header';
import { EmployeeSessionUseCasesService } from '../../app/application/use-cases/employee-session-use-cases.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [FormsModule, HeaderComponent],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.scss',
})
export class LoginPageComponent {
  private readonly themeModeService = inject(ThemeModeService);
  private readonly destroyRef = inject(DestroyRef);

  email = '';
  isSubmitting = false;
  errorText = '';

  constructor(
    private readonly router: Router,
    private readonly sessionUseCases: EmployeeSessionUseCasesService,
    private readonly notifications: NotificationService
  ) {}

  protected get themeMode(): 'light' | 'dark' {
    return this.themeModeService.mode;
  }

  protected onThemeToggle(): void {
    this.themeModeService.toggle();
  }

  register(): void {
    const normalizedEmail = this.email.trim().toLowerCase();
    if (!normalizedEmail || this.isSubmitting) {
      return;
    }

    this.errorText = '';
    this.isSubmitting = true;

    this.sessionUseCases
      .login(normalizedEmail)
      .pipe(
        finalize(() => (this.isSubmitting = false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => {
          void this.router.navigate(['/panel/accounts']);
        },
        error: (error: unknown) => {
          this.errorText = this.resolveErrorText(error);
          this.notifications.error(this.errorText);
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


