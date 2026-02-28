import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthRoleService } from '../../../../shared/auth';
import { HeaderComponent } from '../../../../shared/ui/header';

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
    private readonly authRoleService: AuthRoleService
  ) {}

  logout(): void {
    this.authRoleService.clearRole();
    void this.router.navigateByUrl('/registration');
  }
}
