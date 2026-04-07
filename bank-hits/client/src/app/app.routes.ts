import { Route } from '@angular/router';
import { ErrorFallbackPageComponent } from '../../../shared/frontend-core';
import { roleGuard } from '../../../shared/auth';
import { ClientDashboardPageComponent } from '../pages/dashboard/client-dashboard-page.component';
import { ClientAccountsPageComponent } from '../pages/accounts/client-accounts-page.component';
import { ClientCreditsPageComponent } from '../pages/credits/client-credits-page.component';
import { ClientPanelPageComponent } from '../pages/client-panel/client-panel-page.component';
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
    component: ClientPanelPageComponent,
    canActivate: [roleGuard],
    data: {
      requiredRole: 'client',
      forbiddenRedirect: '/auth/callback',
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
    path: 'error',
    component: ErrorFallbackPageComponent,
  },
  {
    path: '**',
    component: ErrorFallbackPageComponent,
  },
];
