import { Route } from '@angular/router';
import { LoginPageComponent } from '../pages/login/login-page.component';
import { roleGuard } from '../../../shared/auth';
import { EmployeePanelPageComponent } from '../pages/employee-panel/employee-panel-page.component';
import { AccountsPageComponent } from '../pages/panel/accounts/accounts-page.component';
import { CreditsPageComponent } from '../pages/panel/credits/credits-page.component';
import { TariffsPageComponent } from '../pages/panel/tariffs/tariffs-page.component';
import { UsersPageComponent } from '../pages/panel/users/users-page.component';

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
    canActivate: [roleGuard],
    data: {
      requiredRole: 'employee',
      forbiddenRedirect: '/registration',
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'accounts',
      },
      {
        path: 'accounts',
        component: AccountsPageComponent,
      },
      {
        path: 'users',
        component: UsersPageComponent,
      },
      {
        path: 'credits',
        component: CreditsPageComponent,
      },
      {
        path: 'tariffs',
        component: TariffsPageComponent,
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'registration',
  },
];