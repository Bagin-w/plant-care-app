import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { authInterceptor } from './auth-interceptor';
import { AuthService } from '../services/auth.service';

describe('authInterceptor', () => {
  const interceptor: HttpInterceptorFn = (req, next) =>
    TestBed.runInInjectionContext(() => authInterceptor(req, next));

  let authServiceMock: { getToken: ReturnType<typeof vi.fn>; logout: ReturnType<typeof vi.fn> };
  let routerMock: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authServiceMock = { getToken: vi.fn(), logout: vi.fn() };
    routerMock = { navigate: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    });
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('attaches an Authorization header when a token is present', () => {
    authServiceMock.getToken.mockReturnValue('jwt-token');
    const request = new HttpRequest('GET', '/api/plants');
    const next = vi.fn().mockReturnValue(of(null));

    interceptor(request, next);

    const forwarded = next.mock.calls[0][0] as HttpRequest<unknown>;
    expect(forwarded.headers.get('Authorization')).toBe('Bearer jwt-token');
  });

  it('forwards the request unchanged when no token is present', () => {
    authServiceMock.getToken.mockReturnValue(null);
    const request = new HttpRequest('GET', '/api/plants');
    const next = vi.fn().mockReturnValue(of(null));

    interceptor(request, next);

    const forwarded = next.mock.calls[0][0] as HttpRequest<unknown>;
    expect(forwarded).toBe(request);
    expect(forwarded.headers.has('Authorization')).toBe(false);
  });

  it('logs out and redirects to /login on a 401 response', () => {
    authServiceMock.getToken.mockReturnValue('jwt-token');
    const request = new HttpRequest('GET', '/api/plants');
    const error = new HttpErrorResponse({ status: 401 });
    const next = vi.fn().mockReturnValue(throwError(() => error));
    let caught: unknown;

    interceptor(request, next).subscribe({ error: (err) => (caught = err) });

    expect(authServiceMock.logout).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
    expect(caught).toBe(error);
  });

  it('does not log out or redirect on a non-401 error response', () => {
    authServiceMock.getToken.mockReturnValue('jwt-token');
    const request = new HttpRequest('GET', '/api/plants');
    const error = new HttpErrorResponse({ status: 500 });
    const next = vi.fn().mockReturnValue(throwError(() => error));
    let caught: unknown;

    interceptor(request, next).subscribe({ error: (err) => (caught = err) });

    expect(authServiceMock.logout).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
    expect(caught).toBe(error);
  });
});
