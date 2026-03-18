import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { NotificationService, ThemeModeService } from '../../../../shared/frontend-core';
import { HeaderComponent } from '../../../../shared/ui/header';
import { EmployeePanelPageService } from './model';

@Component({
  selector: 'employee-panel-page',
  standalone: true,
  imports: [HeaderComponent, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './employee-panel-page.component.html',
  styleUrl: './employee-panel-page.component.scss',
})
export class EmployeePanelPageComponent {
  protected readonly themeModeService = inject(ThemeModeService);

  constructor(
    private readonly router: Router,
    private readonly employeePanelPageService: EmployeePanelPageService,
    private readonly notifications: NotificationService
  ) {}

  logout(): void {
    this.employeePanelPageService.logout().subscribe({
      next: () => {
        void this.router.navigateByUrl('/registration');
      },
      error: () => {
        this.notifications.error('Не удалось завершить сессию корректно.');
        void this.router.navigateByUrl('/registration');
      },
    });
  }

  protected get themeMode(): 'light' | 'dark' {
    return this.themeModeService.mode;
  }

  protected onThemeToggle(): void {
    this.themeModeService.toggle();
  }
}
