import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  ApplicationConfig,
  ErrorHandler,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { API_BASE_URL, idempotencyKeyInterceptor, usersAuthTokenInterceptor } from 'shared/api';
import {
  appErrorInterceptor,
  GlobalAppErrorHandler,
  MONITORING_APP_NAME,
  resilienceInterceptor,
} from 'shared/frontend-core';
import { appRoutes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(
      withInterceptors([
        usersAuthTokenInterceptor,
        idempotencyKeyInterceptor,
        resilienceInterceptor,
        appErrorInterceptor,
      ])
    ),
    provideRouter(appRoutes),
    { provide: ErrorHandler, useClass: GlobalAppErrorHandler },
    { provide: API_BASE_URL, useValue: 'http://localhost:8085/bff/employee' },
    { provide: MONITORING_APP_NAME, useValue: 'employee' },
  ],
};
