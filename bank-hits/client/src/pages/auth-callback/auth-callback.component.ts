import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthApiService } from '../../../../shared/entities/auth';
import { API_BASE_URL } from '../../../../shared/api';
import { FcmTokenService } from '../../../../shared/frontend-core';

/**
 * Handles the SSO callback.
 * SSO page redirects here with ?token=JWT after successful login.
 * This component stores the token and redirects to the panel.
 */
@Component({
  selector: 'app-auth-callback',
  standalone: true,
  template: `
    <div style="display:flex;align-items:center;justify-content:center;min-height:100vh;font-family:sans-serif;">
      <p>Выполняется вход...</p>
    </div>
  `,
})
export class AuthCallbackComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthApiService);
  private readonly fcmTokenService = inject(FcmTokenService);
  private readonly apiBaseUrl = inject(API_BASE_URL);

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (token && token !== 'undefined' && token !== 'null') {
      this.authService.setToken(token);
      const role = this.authService.extractRoleFromToken(token);
      if (role) {
        this.authService.setRole(role);
      }
      // Register FCM token after login (fire-and-forget)
      void this.fcmTokenService.registerToken(this.apiBaseUrl);
      // Clean URL and redirect to panel
      void this.router.navigate(['/panel'], { replaceUrl: true });
    } else {
      // No token — redirect to SSO login
      this.redirectToSso();
    }
  }

  private redirectToSso(): void {
    const returnUrl = `${window.location.origin}/auth/callback`;
    window.location.href = `http://localhost:4202/login?returnUrl=${encodeURIComponent(returnUrl)}`;
  }
}
