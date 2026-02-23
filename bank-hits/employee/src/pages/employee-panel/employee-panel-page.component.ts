import { Component } from '@angular/core';
import { HeaderComponent } from '../../../../shared/ui/header';

@Component({
  selector: 'employee-panel-page',
  standalone: true,
  imports: [HeaderComponent],
  templateUrl: './employee-panel-page.component.html',
  styleUrl: './employee-panel-page.component.scss',
})
export class EmployeePanelPageComponent {}
