import { Injectable, inject } from '@angular/core';
import { AuthApiService } from 'shared/entities/auth';

@Injectable({ providedIn: 'root' })
export class ClientAuthRequestService {
  private readonly authApi = inject(AuthApiService);

  clearAuth(): void {
    this.authApi.clearAuth();
  }
}
