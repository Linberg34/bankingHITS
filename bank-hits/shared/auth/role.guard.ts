import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthRoleService } from './auth-role.service';
import { UserRole } from './user-role';

export const roleGuard: CanActivateFn = (route) => {
  const roleService = inject(AuthRoleService);
  const router = inject(Router);

  const requiredRole = route.data['requiredRole'] as UserRole | undefined;
  const forbiddenRedirect = (route.data['forbiddenRedirect'] as string | undefined) ?? '/registration';

  if (!requiredRole) {
    return true;
  }

  const currentRole = roleService.getRole();
  if (currentRole === requiredRole) {
    return true;
  }

  return router.parseUrl(forbiddenRedirect);
};
