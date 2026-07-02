import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { Register } from './register';
import { AuthService } from '../../../core/services/auth.service';

describe('Register', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;
  let authServiceMock: {
    register: ReturnType<typeof vi.fn>;
    login: ReturnType<typeof vi.fn>;
    saveToken: ReturnType<typeof vi.fn>;
  };
  let navigateSpy: ReturnType<typeof vi.spyOn>;

  beforeEach(async () => {
    authServiceMock = { register: vi.fn(), login: vi.fn(), saveToken: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
      ],
    }).compileComponents();

    navigateSpy = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('registers, auto-logs in, saves the token, and navigates to /plants on full success', () => {
    authServiceMock.register.mockReturnValue(of({}));
    authServiceMock.login.mockReturnValue(of({ token: 'jwt-token' }));
    component.name = 'Test User';
    component.email = 'test@test.de';
    component.password = 'rawPw';

    component.onSubmit();

    expect(authServiceMock.register).toHaveBeenCalledWith({
      name: 'Test User',
      email: 'test@test.de',
      password: 'rawPw',
    });
    expect(authServiceMock.login).toHaveBeenCalledWith({ email: 'test@test.de', password: 'rawPw' });
    expect(authServiceMock.saveToken).toHaveBeenCalledWith('jwt-token');
    expect(navigateSpy).toHaveBeenCalledWith(['/plants']);
    expect(component.errorMessage()).toBe('');
  });

  it('navigates to /login without an error message when register succeeds but the auto-login fails', () => {
    authServiceMock.register.mockReturnValue(of({}));
    authServiceMock.login.mockReturnValue(throwError(() => ({ status: 401 })));

    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    expect(authServiceMock.saveToken).not.toHaveBeenCalled();
    expect(component.errorMessage()).toBe('');
  });

  it('shows a duplicate-email message when registration fails with 409', () => {
    authServiceMock.register.mockReturnValue(throwError(() => ({ status: 409 })));

    component.onSubmit();

    expect(component.errorMessage()).toBe('Diese E-Mail-Adresse ist bereits registriert.');
    expect(authServiceMock.login).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('shows a generic error message when registration fails with a non-409 status', () => {
    authServiceMock.register.mockReturnValue(throwError(() => ({ status: 500 })));

    component.onSubmit();

    expect(component.errorMessage()).toBe('Registrierung fehlgeschlagen. Bitte versuche es erneut.');
    expect(authServiceMock.login).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
