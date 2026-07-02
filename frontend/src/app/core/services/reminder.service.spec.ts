import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { ReminderService } from './reminder.service';
import { environment } from '../../../environments/environment';
import { ReminderRule } from '../models/reminder.model';

describe('ReminderService', () => {
  let service: ReminderService;
  let httpMock: HttpTestingController;

  const apiUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ReminderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getAllForPlant sends a GET to /plants/:plantId/reminders', () => {
    const rules: ReminderRule[] = [{
      id: 1, plantId: 5, type: 'WATERING', customLabel: null, intervalDays: 3,
      preferredTime: '09:00:00', lastTriggeredAt: null, nextDueAt: '2026-07-05T09:00:00', active: true,
    }];
    let result: ReminderRule[] | undefined;

    service.getAllForPlant(5).subscribe((res) => (result = res));

    const req = httpMock.expectOne(`${apiUrl}/plants/5/reminders`);
    expect(req.request.method).toBe('GET');
    req.flush(rules);

    expect(result).toEqual(rules);
  });

  it('create sends a POST to /plants/:plantId/reminders with the request body', () => {
    const request = { type: 'WATERING' as const, customLabel: null, intervalDays: 3, preferredTime: '09:00:00' };

    service.create(5, request).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/plants/5/reminders`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush({});
  });

  it('deactivate sends a PATCH with an empty body to /reminders/:id/deactivate', () => {
    service.deactivate(2).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/reminders/2/deactivate`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({});
    req.flush(null);
  });

  it('activate sends a PATCH with an empty body to /reminders/:id/activate', () => {
    service.activate(2).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/reminders/2/activate`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({});
    req.flush(null);
  });

  it('delete sends a DELETE to /reminders/:id', () => {
    service.delete(2).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/reminders/2`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
