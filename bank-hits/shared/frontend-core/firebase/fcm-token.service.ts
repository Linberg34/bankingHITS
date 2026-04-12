import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { deleteToken, getToken } from 'firebase/messaging';
import { messaging, VAPID_KEY } from './firebase.config';

@Injectable({ providedIn: 'root' })
export class FcmTokenService {
  private readonly http = inject(HttpClient);

  async registerToken(baseUrl: string): Promise<void> {
    try {
      const permission = await Notification.requestPermission();
      if (permission !== 'granted') return;

      const token = await getToken(messaging, { vapidKey: VAPID_KEY });
      if (!token) return;

      this.http.post(`${baseUrl}/notifications/fcm-token`, { token }).subscribe({
        error: (err) => console.warn('[FCM] token registration request failed', err),
      });
    } catch (e) {
      console.warn('[FCM] token registration failed', e);
    }
  }

  async unregisterToken(baseUrl: string): Promise<void> {
    try {
      const token = await getToken(messaging, { vapidKey: VAPID_KEY });
      if (!token) return;

      this.http
        .delete(`${baseUrl}/notifications/fcm-token`, { body: { token } })
        .subscribe({ error: () => undefined });

      await deleteToken(messaging);
    } catch (e) {
      console.warn('[FCM] token unregister failed', e);
    }
  }
}
