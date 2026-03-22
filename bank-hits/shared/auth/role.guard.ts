import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthApiService } from '../entities/auth';
import { UserRole } from './user-role';

const SSO_LOGIN_URL = 'http://localhost:4202/login';

export const roleGuard: CanActivateFn = (route) => {
  const authApiService = inject(AuthApiService);
  const router = inject(Router);

  const requiredRole = route.data['requiredRole'] as UserRole | undefined;

  // If no token at all — redirect to SSO login
  const token = authApiService.getToken();
  if (!token) {
    const returnUrl = `${window.location.origin}/auth/callback`;
    window.location.href = `${SSO_LOGIN_URL}?returnUrl=${encodeURIComponent(returnUrl)}`;
    return false;
  }

  if (!requiredRole) {
    return true;
  }

  const currentRole = authApiService.getRole();
  if (currentRole === requiredRole) {
    return true;
  }

  // Has token but wrong role — redirect to SSO to re-login
  const returnUrl = `${window.location.origin}/auth/callback`;
  window.location.href = `${SSO_LOGIN_URL}?returnUrl=${encodeURIComponent(returnUrl)}`;
  return false;
};
