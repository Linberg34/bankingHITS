import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { mapHttpError } from './error-mapper';

export const appErrorInterceptor: HttpInterceptorFn = (request, next) =>
  next(request).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse) {
        return throwError(() => mapHttpError(error));
      }

      return throwError(() => error);
    })
  );

