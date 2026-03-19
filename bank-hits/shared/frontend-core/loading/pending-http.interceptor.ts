import { HttpContextToken, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';
import { PendingRequestsService } from './pending-requests.service';

export const SKIP_PENDING_REQUEST = new HttpContextToken<boolean>(() => false);

export const pendingHttpInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.context.get(SKIP_PENDING_REQUEST)) {
    return next(req);
  }

  const pendingRequests = inject(PendingRequestsService);
  pendingRequests.start();

  return next(req).pipe(finalize(() => pendingRequests.finish()));
};
