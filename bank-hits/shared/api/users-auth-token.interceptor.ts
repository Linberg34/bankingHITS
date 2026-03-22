import { HttpInterceptorFn } from '@angular/common/http';

export const AUTH_TOKEN_STORAGE_KEY = 'auth_token';

/**
 * Adds Authorization: Bearer <token> header to all BFF requests.
 * Token is issued by SSO service and stored in localStorage.
 */
export const usersAuthTokenInterceptor: HttpInterceptorFn = (request, next) => {
  const url = request.url;

  // Add token to all BFF requests
  const isBffRequest = url.includes('/bff/');
  if (!isBffRequest) {
    return next(request);
  }

  const token = readAuthToken();
  if (!token) {
    return next(request);
  }

  return next(
    request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    })
  );
};

function readAuthToken(): string | null {
  try {
    return globalThis.localStorage?.getItem(AUTH_TOKEN_STORAGE_KEY) ?? null;
  } catch {
    return null;
  }
}
