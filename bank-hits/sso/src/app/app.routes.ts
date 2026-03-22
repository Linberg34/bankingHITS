import { Routes } from '@angular/router';

export const appRoutes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('../pages/sso-login/sso-login-page.component').then(
        (m) => m.SsoLoginPageComponent
      ),
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
];
