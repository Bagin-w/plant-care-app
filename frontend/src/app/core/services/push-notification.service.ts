import { Injectable } from '@angular/core';
import { SwPush } from '@angular/service-worker';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PushNotificationService {

  private apiUrl = `${environment.apiUrl}/devices`;

  constructor(
    private swPush: SwPush,
    private http: HttpClient
  ) {}

  subscribeToPush(): void {
    if (!this.swPush.isEnabled) {
      console.warn('Push-Benachrichtigungen werden von diesem Browser/Modus nicht unterstützt.');
      return;
    }

    this.swPush.requestSubscription({
      serverPublicKey: environment.vapidPublicKey
    }).then(subscription => {
      this.sendSubscriptionToBackend(subscription);
    }).catch(err => {
      console.error('Fehler beim Abonnieren von Push-Benachrichtigungen:', err);
    });
  }

  private sendSubscriptionToBackend(subscription: PushSubscription): void {
    const key = subscription.getKey('p256dh');
    const auth = subscription.getKey('auth');

    const p256dh = key ? this.arrayBufferToBase64(key) : '';
    const authKey = auth ? this.arrayBufferToBase64(auth) : '';

    this.http.post(this.apiUrl, {
      endpoint: subscription.endpoint,
      p256dh: p256dh,
      auth: authKey
    }).subscribe({
      next: () => console.log('Gerät erfolgreich registriert für Push-Benachrichtigungen.'),
      error: (err) => console.error('Fehler beim Registrieren des Geräts:', err)
    });
  }

  private arrayBufferToBase64(buffer: ArrayBuffer): string {
    const bytes = new Uint8Array(buffer);
    let binary = '';
    for (let i = 0; i < bytes.byteLength; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary)
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
  }
}
