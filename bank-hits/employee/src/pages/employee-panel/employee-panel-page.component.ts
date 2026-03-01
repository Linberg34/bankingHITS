import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
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
    private readonly employeePanelPageService: EmployeePanelPageService
  ) {}

  logout(): void {
    this.employeePanelPageService.logout().subscribe({
      next: () => void this.router.navigateByUrl('/registration'),
      error: () => void this.router.navigateByUrl('/registration'),
    });
  }
}
