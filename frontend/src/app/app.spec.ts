import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { App } from './app';

describe('App', () => {
  let component: App;
  let fixture: ComponentFixture<App>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(App);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('showNavigation hides the nav bar on /login and /register', () => {
    component.router = { url: '/login' } as Router;
    expect(component.showNavigation()).toBe(false);

    component.router = { url: '/register' } as Router;
    expect(component.showNavigation()).toBe(false);
  });

  it('showNavigation shows the nav bar on other routes', () => {
    component.router = { url: '/plants' } as Router;
    expect(component.showNavigation()).toBe(true);
  });
});
