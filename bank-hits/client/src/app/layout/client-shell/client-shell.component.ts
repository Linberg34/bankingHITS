import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { HeaderComponent } from '../../../../../shared/ui/header';
import { AuthApiService } from '../../core/services/auth-api.service';
import { AuthRoleService } from '../../../../../shared/auth';

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
  private readonly authApi = inject(AuthApiService);
  private readonly authRole = inject(AuthRoleService);

  protected pageTitle = (this.route.snapshot.data['title'] as string) ?? 'Клиент';
  protected headerTitle = 'Интернет-Банк - ' + this.pageTitle;

  protected navItems = [
    { path: 'dashboard', label: 'Главная' },
    { path: 'accounts', label: 'Мои счета' },
    { path: 'credits', label: 'Кредиты' },
  ];

  protected onLogout(): void {
    this.authApi.logout().subscribe({
      next: () => {
        this.authRole.clearRole();
        void this.router.navigate(['/registration']);
      },
      error: () => {
        this.authRole.clearRole();
        void this.router.navigate(['/registration']);
      },
    });
  }
}
