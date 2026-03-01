import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthApiService } from '../entities/auth';
import { UserRole } from './user-role';

export const roleGuard: CanActivateFn = (route) => {
  const authApiService = inject(AuthApiService);
  const router = inject(Router);

  const requiredRole = route.data['requiredRole'] as UserRole | undefined;
  const forbiddenRedirect = (route.data['forbiddenRedirect'] as string | undefined) ?? '/registration';

  if (!requiredRole) {
    return true;
  }

  const currentRole = authApiService.getRole();
  if (currentRole === requiredRole) {
    return true;
  }

  return router.parseUrl(forbiddenRedirect);
};
