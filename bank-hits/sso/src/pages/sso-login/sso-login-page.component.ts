import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

const SSO_API = 'http://localhost:8086';

interface SsoTokenResponse {
  access_token: string;
  expires_in: number;
  token_type: string;
}

@Component({
  selector: 'app-sso-login-page',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <div class="sso-container">
      <div class="sso-card">
        <h1 class="sso-title">Банк</h1>
        <p class="sso-subtitle">Единый вход в банковские сервисы</p>

        <div class="sso-tabs">
          <button
            class="sso-tab"
            [class.active]="activeTab() === 'login'"
            (click)="activeTab.set('login')"
          >
            Войти
          </button>
          <button
            class="sso-tab"
            [class.active]="activeTab() === 'register'"
            (click)="activeTab.set('register')"
          >
            Регистрация
          </button>
        </div>

        @if (activeTab() === 'login') {
          <form class="sso-form" (ngSubmit)="onLogin()" #loginForm="ngForm">
            <div class="sso-field">
              <label for="username">Логин</label>
              <input
                id="username"
                name="username"
                type="text"
                autocomplete="username"
                [(ngModel)]="loginUsername"
                required
                placeholder="example@bank.ru"
                [class.error]="loginError()"
              />
            </div>
            <div class="sso-field">
              <label for="password">Пароль</label>
              <input
                id="password"
                name="password"
                type="password"
                autocomplete="current-password"
                [(ngModel)]="loginPassword"
                required
                placeholder="••••••••"
                [class.error]="loginError()"
              />
            </div>

            @if (loginError()) {
              <div class="sso-error">{{ loginError() }}</div>
            }

            <button
              type="submit"
              class="sso-button"
              [disabled]="loginLoading() || !loginUsername || !loginPassword"
            >
              {{ loginLoading() ? 'Вход...' : 'Войти' }}
            </button>
          </form>
        }

        @if (activeTab() === 'register') {
          <form class="sso-form" (ngSubmit)="onRegister()" #regForm="ngForm">
            <div class="sso-field">
              <label for="reg-name">Имя</label>
              <input
                id="reg-name"
                name="name"
                type="text"
                [(ngModel)]="regName"
                required
                placeholder="Иван Иванов"
              />
            </div>
            <div class="sso-field">
              <label for="reg-username">Логин (email)</label>
              <input
                id="reg-username"
                name="regUsername"
                type="text"
                autocomplete="username"
                [(ngModel)]="regUsername"
                required
                placeholder="ivan@bank.ru"
              />
            </div>
            <div class="sso-field">
              <label for="reg-password">Пароль</label>
              <input
                id="reg-password"
                name="regPassword"
                type="password"
                autocomplete="new-password"
                [(ngModel)]="regPassword"
                required
                minlength="6"
                placeholder="Минимум 6 символов"
              />
            </div>

            @if (registerError()) {
              <div class="sso-error">{{ registerError() }}</div>
            }
            @if (registerSuccess()) {
              <div class="sso-success">{{ registerSuccess() }}</div>
            }

            <button
              type="submit"
              class="sso-button"
              [disabled]="registerLoading() || !regName || !regUsername || !regPassword"
            >
              {{ registerLoading() ? 'Регистрация...' : 'Зарегистрироваться' }}
            </button>
          </form>
        }
      </div>
    </div>
  `,
})
export class SsoLoginPageComponent {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);

  activeTab = signal<'login' | 'register'>('login');

  // Login form state
  loginUsername = '';
  loginPassword = '';
  loginLoading = signal(false);
  loginError = signal<string | null>(null);

  // Register form state
  regName = '';
  regUsername = '';
  regPassword = '';
  registerLoading = signal(false);
  registerError = signal<string | null>(null);
  registerSuccess = signal<string | null>(null);

  onLogin(): void {
    this.loginError.set(null);
    this.loginLoading.set(true);

    this.http
      .post<SsoTokenResponse>(`${SSO_API}/auth/login`, {
        username: this.loginUsername,
        password: this.loginPassword,
      })
      .subscribe({
        next: (response) => {
          this.loginLoading.set(false);
          this.redirectWithToken(response.access_token);
        },
        error: (err) => {
          this.loginLoading.set(false);
          const msg = err?.error?.message ?? err?.error ?? 'Неверный логин или пароль';
          this.loginError.set(typeof msg === 'string' ? msg : 'Ошибка входа');
        },
      });
  }

  onRegister(): void {
    this.registerError.set(null);
    this.registerSuccess.set(null);
    this.registerLoading.set(true);

    this.http
      .post(`${SSO_API}/auth/register`, {
        name: this.regName,
        username: this.regUsername,
        password: this.regPassword,
        roles: ['CLIENT'],
      })
      .subscribe({
        next: () => {
          this.registerLoading.set(false);
          this.registerSuccess.set('Регистрация успешна! Теперь войдите.');
          this.loginUsername = this.regUsername;
          this.activeTab.set('login');
        },
        error: (err) => {
          this.registerLoading.set(false);
          const msg = err?.error?.message ?? err?.error ?? 'Ошибка регистрации';
          this.registerError.set(typeof msg === 'string' ? msg : 'Ошибка регистрации');
        },
      });
  }

  private redirectWithToken(token: string): void {
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
    if (returnUrl) {
      // Redirect back to the calling app with token in query param
      const separator = returnUrl.includes('?') ? '&' : '?';
      window.location.href = `${returnUrl}${separator}token=${encodeURIComponent(token)}`;
    } else {
      // Fallback: default to client app
      window.location.href = `http://localhost:4200/auth/callback?token=${encodeURIComponent(token)}`;
    }
  }
}
