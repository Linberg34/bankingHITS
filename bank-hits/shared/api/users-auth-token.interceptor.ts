import { HttpInterceptorFn } from '@angular/common/http';

const PROTECTED_API_PATHS = ['/api/auth', '/api/users', '/api/account', '/api/tariffs', '/api/credits'];

const AUTH_TOKEN_STORAGE_KEY = 'auth_token';

const AUTH_PATHS_WITHOUT_TOKEN = ['/api/auth/login', '/api/auth/register'];

export const usersAuthTokenInterceptor: HttpInterceptorFn = (request, next) => {
  const url = request.url;
  const noTokenForThisPath = AUTH_PATHS_WITHOUT_TOKEN.some((path) => url.includes(path));
  if (noTokenForThisPath) {
    return next(request);
  }

  const isProtectedRequest = PROTECTED_API_PATHS.some((path) => url.includes(path));
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

