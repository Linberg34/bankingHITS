import { HttpInterceptorFn } from '@angular/common/http';

const PROTECTED_API_PATHS = ['/api/users', '/api/account', '/api/tariffs', '/api/credits'];
const AUTH_TOKEN_STORAGE_KEY = 'auth_token';

export const usersAuthTokenInterceptor: HttpInterceptorFn = (request, next) => {
  const isProtectedRequest = PROTECTED_API_PATHS.some((path) => request.url.includes(path));
  if (!isProtectedRequest) {
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
