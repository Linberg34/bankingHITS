import { Route } from '@angular/router';
import { EmployeePanelPageComponent } from '../pages/employee-panel/employee-panel-page.component';
import { LoginPageComponent } from '../pages/login/login-page.component';

export const appRoutes: Route[] = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'registration',
  },
  {
    path: 'registration',
    component: LoginPageComponent,
  },
  {
    path: 'panel',
    component: EmployeePanelPageComponent,
  },
  {
    path: '**',
    redirectTo: 'registration',
  },
];
