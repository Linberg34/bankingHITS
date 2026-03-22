import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ThemeModeService } from '../../../../shared/frontend-core';
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

  constructor(private readonly employeePanelPageService: EmployeePanelPageService) {}

  logout(): void {
    this.employeePanelPageService.logout();
  }

  protected get themeMode(): 'light' | 'dark' {
    return this.themeModeService.mode;
  }

  protected onThemeToggle(): void {
    this.themeModeService.toggle();
  }
}
