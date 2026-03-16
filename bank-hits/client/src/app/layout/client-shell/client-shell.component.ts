import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { HeaderComponent } from '../../../../../shared/ui/header';
import { ClientSessionUseCasesService } from '../../application/use-cases/client-session-use-cases.service';
import { NotificationService } from '../../../../../shared/frontend-core';

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

  protected pageTitle = (this.route.snapshot.data['title'] as string) ?? 'Клиент';
  protected headerTitle = 'Интернет-Банк - ' + this.pageTitle;

  protected navItems = [
    { path: 'dashboard', label: 'Главная' },
    { path: 'accounts', label: 'Мои счета' },
    { path: 'credits', label: 'Кредиты' },
  ];

  protected onLogout(): void {
    this.sessionUseCases.logout().subscribe({
      next: () => {
        void this.router.navigate(['/registration']);
      },
      error: () => {
        this.sessionUseCases.clearSession();
        this.notifications.error('Logout failed. Session was cleared locally.');
        void this.router.navigate(['/registration']);
      },
    });
  }
}
