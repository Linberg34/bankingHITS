import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  ApplicationConfig,
  ErrorHandler,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { usersAuthTokenInterceptor } from '../../../shared/api';
import { appErrorInterceptor, GlobalAppErrorHandler } from '../../../shared/frontend-core';
import { appRoutes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([usersAuthTokenInterceptor, appErrorInterceptor])),
    provideRouter(appRoutes),
    { provide: ErrorHandler, useClass: GlobalAppErrorHandler },
  ],
};
