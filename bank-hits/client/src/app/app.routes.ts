import { Route } from '@angular/router';
import { roleGuard } from '../../../shared/auth';
import { ClientPanelPageComponent } from '../pages/client-panel/client-panel-page.component';
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
    component: ClientPanelPageComponent,
    canActivate: [roleGuard],
    data: {
      requiredRole: 'client',
      forbiddenRedirect: '/registration',
    },
  },
  {
    path: '**',
    redirectTo: 'registration',
  },
];
