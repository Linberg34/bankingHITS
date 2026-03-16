import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { NotificationService } from '../../../../shared/frontend-core';
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
  constructor(
    private readonly router: Router,
    private readonly employeePanelPageService: EmployeePanelPageService,
    private readonly notifications: NotificationService
  ) {}

  logout(): void {
    this.employeePanelPageService.logout().subscribe({
      next: () => {
        this.notifications.info('Вы вышли из системы.');
        void this.router.navigateByUrl('/registration');
      },
      error: () => {
        this.notifications.error('Не удалось завершить сессию корректно.');
        void this.router.navigateByUrl('/registration');
      },
    });
  }
}

