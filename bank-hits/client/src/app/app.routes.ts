import { Route } from '@angular/router';
import { roleGuard } from '../../../shared/auth';
import { ClientPanelPageComponent } from '../pages/client-panel/client-panel-page.component';
import { ClientLoginPageComponent } from '../pages/login/client-login-page.component';
import { ClientAccountsPageComponent } from '../pages/accounts/client-accounts-page.component';
import { ClientCreditsPageComponent } from '../pages/credits/client-credits-page.component';
import { ClientDashboardPageComponent } from '../pages/dashboard/client-dashboard-page.component';

export const appRoutes: Route[] = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'registration',
  },
  {
    path: 'registration',
    component: ClientLoginPageComponent,
  },
  {
    path: 'panel',
    component: ClientPanelPageComponent,
    canActivate: [roleGuard],
    data: {
      requiredRole: 'client',
      forbiddenRedirect: '/registration',
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        component: ClientDashboardPageComponent,
        data: { title: 'Обзор счетов' },
      },
      {
        path: 'accounts',
        component: ClientAccountsPageComponent,
        data: { title: 'Управление счетами' },
      },
      {
        path: 'credits',
        component: ClientCreditsPageComponent,
        data: { title: 'Мои кредиты' },
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'registration',
  },
];
