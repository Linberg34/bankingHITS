import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { deleteToken, getToken } from 'firebase/messaging';
import { messaging, VAPID_KEY } from './firebase.config';

const FCM_TOKEN_KEY = 'fcm_token';

@Injectable({ providedIn: 'root' })
export class FcmTokenService {
  private readonly http = inject(HttpClient);

  async registerToken(baseUrl: string): Promise<void> {
    try {
      const permission = await Notification.requestPermission();
      if (permission !== 'granted') return;

      const registration = await navigator.serviceWorker.register('/firebase-messaging-sw.js');
      const newToken = await getToken(messaging, { vapidKey: VAPID_KEY, serviceWorkerRegistration: registration });
      if (!newToken) return;

      const oldToken = localStorage.getItem(FCM_TOKEN_KEY);

      if (oldToken && oldToken !== newToken) {
        this.http
          .delete(`${baseUrl}/notifications/fcm-token`, { body: { token: oldToken } })
          .subscribe({ error: () => undefined });
      }

      if (newToken !== oldToken) {
        localStorage.setItem(FCM_TOKEN_KEY, newToken);
        this.http.post(`${baseUrl}/notifications/fcm-token`, { token: newToken }).subscribe({
          error: (err) => console.warn('[FCM] token registration request failed', err),
        });
      }
    } catch (e) {
      console.warn('[FCM] token registration failed', e);
    }
  }

  async unregisterToken(baseUrl: string): Promise<void> {
    try {
      const registration = await navigator.serviceWorker.register('/firebase-messaging-sw.js');
      const token = await getToken(messaging, { vapidKey: VAPID_KEY, serviceWorkerRegistration: registration });
      if (!token) return;

      this.http
        .delete(`${baseUrl}/notifications/fcm-token`, { body: { token } })
        .subscribe({ error: () => undefined });

      localStorage.removeItem(FCM_TOKEN_KEY);
      await deleteToken(messaging);
    } catch (e) {
      console.warn('[FCM] token unregister failed', e);
    }
  }
}
