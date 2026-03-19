import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NotificationService, ThemeModeService } from '../../../../../shared/frontend-core';
import { HeaderComponent } from '../../../../../shared/ui/header';
import { ClientSessionUseCasesService } from '../../application/use-cases/client-session-use-cases.service';

@Component({
  selector: 'app-client-shell',
  standalone: true,
  imports: [HeaderComponent, RouterLink, RouterLinkActive],
  templateUrl: './client-shell.component.html',
  styleUrl: './client-shell.component.scss',
})
export class ClientShellComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly sessionUseCases = inject(ClientSessionUseCasesService);
  private readonly notifications = inject(NotificationService);
  private readonly themeModeService = inject(ThemeModeService);
  private readonly destroyRef = inject(DestroyRef);

  protected pageTitle = (this.route.snapshot.data['title'] as string) ?? 'Клиент';
  protected headerTitle = 'Интернет-Банк - ' + this.pageTitle;

  protected navItems = [
    { path: 'dashboard', label: 'Главная' },
    { path: 'accounts', label: 'Мои счета' },
    { path: 'credits', label: 'Кредиты' },
  ];

  protected get themeMode(): 'light' | 'dark' {
    return this.themeModeService.mode;
  }

  protected onLogout(): void {
    this.sessionUseCases
      .logout()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
      next: () => {
        void this.router.navigate(['/registration']);
      },
      error: () => {
        this.sessionUseCases.clearSession();
        this.notifications.error('Не удалось выйти. Сессия очищена локально.');
        void this.router.navigate(['/registration']);
      },
      });
  }

  protected onThemeToggle(): void {
    this.themeModeService.toggle();
  }
}
