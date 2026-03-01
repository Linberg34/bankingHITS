import { InjectionToken } from '@angular/core';
import { DEFAULT_API_BASE_URL } from './secrets';

interface GlobalApiConfig {
  __BANK_HITS_API_URL__?: string;
}

export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  providedIn: 'root',
  factory: () => {
    const globalConfig = globalThis as GlobalApiConfig;
    return globalConfig.__BANK_HITS_API_URL__ ?? DEFAULT_API_BASE_URL;
  },
});
