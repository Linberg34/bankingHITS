import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthApiService } from '../../../../shared/entities/auth';
import { FcmTokenService } from '../../../../shared/frontend-core';

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

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (token) {
      this.authService.setToken(token);
      const role = this.authService.extractRoleFromToken(token);
      if (role) {
        this.authService.setRole(role);
      }
      // Register FCM token after login (fire-and-forget)
      void this.fcmTokenService.registerToken('http://sof-kov.ru:8085/bff/employee');
      void this.router.navigate(['/panel'], { replaceUrl: true });
    } else {
      this.redirectToSso();
    }
  }

  private redirectToSso(): void {
    const returnUrl = `${window.location.origin}/auth/callback`;
    window.location.href = `http://localhost:4202/login?returnUrl=${encodeURIComponent(returnUrl)}`;
  }
}
