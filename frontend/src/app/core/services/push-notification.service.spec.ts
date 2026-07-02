import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SwPush } from '@angular/service-worker';
import { vi } from 'vitest';

import { PushNotificationService } from './push-notification.service';
import { environment } from '../../../environments/environment';

describe('PushNotificationService', () => {
  let service: PushNotificationService;
  let httpMock: HttpTestingController;
  let swPushMock: { isEnabled: boolean; requestSubscription: ReturnType<typeof vi.fn> };

  const apiUrl = `${environment.apiUrl}/devices`;

  beforeEach(() => {
    swPushMock = { isEnabled: true, requestSubscription: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SwPush, useValue: swPushMock },
      ],
    });
    service = TestBed.inject(PushNotificationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('does not request a subscription when push is not enabled', () => {
    swPushMock.isEnabled = false;

    service.subscribeToPush();

    expect(swPushMock.requestSubscription).not.toHaveBeenCalled();
    httpMock.expectNone(apiUrl);
  });

  it('sends the base64url-encoded subscription keys to /devices on success', async () => {
    const subscription = {
      endpoint: 'https://push.example.com/sub/abc',
      getKey: (name: string) =>
        name === 'p256dh' || name === 'auth' ? new Uint8Array([0, 0, 0]).buffer : null,
    } as unknown as PushSubscription;
    const requestPromise = Promise.resolve(subscription);
    swPushMock.requestSubscription.mockReturnValue(requestPromise);

    service.subscribeToPush();
    await requestPromise;
    await Promise.resolve();

    expect(swPushMock.requestSubscription).toHaveBeenCalledWith({
      serverPublicKey: environment.vapidPublicKey,
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      endpoint: 'https://push.example.com/sub/abc',
      p256dh: 'AAAA',
      auth: 'AAAA',
    });
    req.flush({});
  });

  it('sends empty key strings when the subscription has no p256dh/auth keys', async () => {
    const subscription = {
      endpoint: 'https://push.example.com/sub/abc',
      getKey: () => null,
    } as unknown as PushSubscription;
    const requestPromise = Promise.resolve(subscription);
    swPushMock.requestSubscription.mockReturnValue(requestPromise);

    service.subscribeToPush();
    await requestPromise;
    await Promise.resolve();

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.body).toEqual({
      endpoint: 'https://push.example.com/sub/abc',
      p256dh: '',
      auth: '',
    });
    req.flush({});
  });

  it('does not call the backend when requesting the subscription fails', async () => {
    const rejection = Promise.reject(new Error('permission denied'));
    swPushMock.requestSubscription.mockReturnValue(rejection);

    service.subscribeToPush();
    await rejection.catch(() => {});
    await Promise.resolve();

    httpMock.expectNone(apiUrl);
  });
});
