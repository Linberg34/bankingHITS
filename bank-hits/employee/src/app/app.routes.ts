import { Route } from '@angular/router';
import { roleGuard } from '../../../shared/auth';
import { EmployeePanelPageComponent } from '../pages/employee-panel/employee-panel-page.component';
import { AccountsPageComponent } from '../pages/panel/accounts/accounts-page.component';
import { CreditsPageComponent } from '../pages/panel/credits/credits-page.component';
import { TariffsPageComponent } from '../pages/panel/tariffs/tariffs-page.component';
import { UsersPageComponent } from '../pages/panel/users/users-page.component';
import { ErrorFallbackPageComponent } from '../../../shared/frontend-core';
import { AuthCallbackComponent } from '../pages/auth-callback/auth-callback.component';

export const appRoutes: Route[] = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'panel',
  },
  {
    path: 'auth/callback',
    component: AuthCallbackComponent,
  },
  {
    path: 'panel',
    component: EmployeePanelPageComponent,
    canActivate: [roleGuard],
    data: {
      requiredRole: 'employee',
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
    path: 'error',
    component: ErrorFallbackPageComponent,
  },
  {
    path: '**',
    component: ErrorFallbackPageComponent,
  },
];
