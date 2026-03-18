import { Component, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ThemeModeService } from '../../../../shared/frontend-core';
import { HeaderComponent } from '../../../../shared/ui/header';
import { ClientLoginPageService } from './client-login-page.service';

@Component({
  selector: 'app-client-login-page',
  standalone: true,
  imports: [FormsModule, HeaderComponent],
  templateUrl: './client-login-page.component.html',
  styleUrl: './client-login-page.component.scss',
})
export class ClientLoginPageComponent {
  private readonly loginService = inject(ClientLoginPageService);
  private readonly themeModeService = inject(ThemeModeService);

  protected email = signal('');
  protected error = signal('');
  protected loading = signal(false);

  protected get themeMode(): 'light' | 'dark' {
    return this.themeModeService.mode;
  }

  protected onThemeToggle(): void {
    this.themeModeService.toggle();
  }

  submit(form: NgForm): void {
    if (form.invalid) {
      form.control.markAllAsTouched();
      const control = form.control.get('email');
      if (control?.errors?.['required']) {
        this.error.set('Введите email');
      } else if (control?.errors?.['pattern']) {
        this.error.set('Введите корректный email');
      } else {
        this.error.set('Заполните поле корректно');
      }
      return;
    }

    const value = this.email().trim();
    this.error.set('');
    this.loading.set(true);
    this.loginService.login(value).subscribe({
      next: (result) => {
        this.loading.set(false);
        if (!result.success) {
          this.error.set(result.error);
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Ошибка соединения');
      },
    });
  }
}
