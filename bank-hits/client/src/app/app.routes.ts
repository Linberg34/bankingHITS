import { Route } from '@angular/router';
import { ClientLoginPageComponent } from '../pages/login/client-login-page.component';
import { ClientAccountsPageComponent } from '../pages/accounts/client-accounts-page.component';
import { ClientCreditsPageComponent } from '../pages/credits/client-credits-page.component';
import { ClientDashboardPageComponent } from '../pages/dashboard/client-dashboard-page.component';

export const appRoutes: Route[] = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: 'login',
    component: ClientLoginPageComponent,
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
  {
    path: '**',
    redirectTo: '',
  },
];
