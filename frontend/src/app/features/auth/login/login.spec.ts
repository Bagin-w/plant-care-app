import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { Login } from './login';
import { AuthService } from '../../../core/services/auth.service';
import { PushNotificationService } from '../../../core/services/push-notification.service';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authServiceMock: { login: ReturnType<typeof vi.fn>; saveToken: ReturnType<typeof vi.fn> };
  let pushNotificationServiceMock: { subscribeToPush: ReturnType<typeof vi.fn> };
  let navigateSpy: ReturnType<typeof vi.spyOn>;

  beforeEach(async () => {
    authServiceMock = { login: vi.fn(), saveToken: vi.fn() };
    pushNotificationServiceMock = { subscribeToPush: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
        { provide: PushNotificationService, useValue: pushNotificationServiceMock },
      ],
    }).compileComponents();

    navigateSpy = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('onSubmit saves the token, subscribes to push, and navigates to /plants on success', () => {
    authServiceMock.login.mockReturnValue(of({ token: 'jwt-token' }));
    component.email = 'test@test.de';
    component.password = 'rawPw';

    component.onSubmit();

    expect(authServiceMock.login).toHaveBeenCalledWith({ email: 'test@test.de', password: 'rawPw' });
    expect(authServiceMock.saveToken).toHaveBeenCalledWith('jwt-token');
    expect(pushNotificationServiceMock.subscribeToPush).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/plants']);
    expect(component.errorMessage()).toBe('');
  });

  it('onSubmit sets a generic error message and does not navigate when login fails', () => {
    authServiceMock.login.mockReturnValue(throwError(() => ({ status: 401 })));

    component.onSubmit();

    expect(component.errorMessage()).toBe('Login fehlgeschlagen. Bitte prüfe deine Zugangsdaten.');
    expect(authServiceMock.saveToken).not.toHaveBeenCalled();
    expect(pushNotificationServiceMock.subscribeToPush).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
