import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterLinkActive } from '@angular/router';
import { ThemeModeService } from '../../../../../shared/frontend-core';
import { HeaderComponent } from '../../../../../shared/ui/header';
import { SettingsApiService } from '../../../../../shared/entities/settings';
import { ClientSessionUseCasesService } from '../../application/use-cases/client-session-use-cases.service';

@Component({
  selector: 'app-client-shell',
  standalone: true,
  imports: [HeaderComponent, RouterLink, RouterLinkActive],
  templateUrl: './client-shell.component.html',
  styleUrl: './client-shell.component.scss',
})
export class ClientShellComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly sessionUseCases = inject(ClientSessionUseCasesService);
  private readonly themeModeService = inject(ThemeModeService);
  private readonly settingsApi = inject(SettingsApiService);

  protected pageTitle = (this.route.snapshot.data['title'] as string) ?? 'Клиент';
  protected headerTitle = 'Интернет-Банк - ' + this.pageTitle;

  protected navItems = [
    { path: 'dashboard', label: 'Главная' },
    { path: 'accounts', label: 'Мои счета' },
    { path: 'credits', label: 'Кредиты' },
  ];

  ngOnInit(): void {
    this.settingsApi.getSettings().subscribe({
      next: (settings) => {
        if (settings.theme) {
          this.themeModeService.setMode(settings.theme);
        }
      },
    });
  }

  protected get themeMode(): 'light' | 'dark' {
    return this.themeModeService.mode;
  }

  protected onLogout(): void {
    this.sessionUseCases.logout();
  }

  protected onThemeToggle(): void {
    const newMode = this.themeModeService.mode === 'light' ? 'dark' : 'light';
    this.themeModeService.setMode(newMode);
    this.settingsApi.updateSettings({ theme: newMode }).subscribe();
  }
}
