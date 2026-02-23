import { Route } from '@angular/router';
import { ClientLayoutComponent } from './layout/client-layout/client-layout.component';
import { ClientAccountsPageComponent } from '../pages/accounts/client-accounts-page.component';
import { ClientCreditsPageComponent } from '../pages/credits/client-credits-page.component';
import { ClientDashboardPageComponent } from '../pages/dashboard/client-dashboard-page.component';

export const appRoutes: Route[] = [
  {
    path: '',
    component: ClientLayoutComponent,
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
    redirectTo: '',
  },
];
