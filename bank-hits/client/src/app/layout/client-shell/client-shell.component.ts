import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { HeaderComponent } from '../../../../../shared/ui/header';

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

  protected pageTitle = (this.route.snapshot.data['title'] as string) ?? 'Клиент';
  protected headerTitle = 'Интернет-Банк - ' + this.pageTitle;

  protected navItems = [
    { path: 'dashboard', label: 'Главная' },
    { path: 'accounts', label: 'Мои счета' },
    { path: 'credits', label: 'Кредиты' },
  ];

  protected onLogout(): void {
    void this.router.navigate(['/login']);
  }
}
