import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('login posts credentials to /auth/login and returns the token response', () => {
    const request = { email: 'test@test.de', password: 'rawPw' };
    let response: { token: string } | undefined;

    service.login(request).subscribe((res) => (response = res));

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush({ token: 'jwt-token' });

    expect(response).toEqual({ token: 'jwt-token' });
  });

  it('register posts to /users, not /auth/register', () => {
    const request = { email: 'test@test.de', password: 'rawPw', name: 'Test User' };

    service.register(request).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/users`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush({});
  });

  it('saveToken stores the token under the "token" key in localStorage', () => {
    service.saveToken('abc123');

    expect(localStorage.getItem('token')).toBe('abc123');
  });

  it('getToken reads the token back from localStorage', () => {
    localStorage.setItem('token', 'xyz789');

    expect(service.getToken()).toBe('xyz789');
  });

  it('getToken returns null when no token is stored', () => {
    expect(service.getToken()).toBeNull();
  });

  it('logout removes the token from localStorage', () => {
    localStorage.setItem('token', 'abc123');

    service.logout();

    expect(localStorage.getItem('token')).toBeNull();
  });

  it('isLoggedIn returns true when a token is present', () => {
    localStorage.setItem('token', 'abc123');

    expect(service.isLoggedIn()).toBe(true);
  });

  it('isLoggedIn returns false when no token is present', () => {
    expect(service.isLoggedIn()).toBe(false);
  });
});
