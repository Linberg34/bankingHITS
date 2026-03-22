import {
  ApplicationConfig,
  ErrorHandler,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { appRoutes } from './app.routes';
import { API_BASE_URL, usersAuthTokenInterceptor } from '../../../shared/api';
import { appErrorInterceptor, GlobalAppErrorHandler } from '../../../shared/frontend-core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(appRoutes),
    provideHttpClient(withInterceptors([usersAuthTokenInterceptor, appErrorInterceptor])),
    { provide: ErrorHandler, useClass: GlobalAppErrorHandler },
    { provide: API_BASE_URL, useValue: 'http://localhost:8084/bff/client' },
  ],
};
