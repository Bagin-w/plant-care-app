import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { CareProfileService } from './care-profile.service';
import { environment } from '../../../environments/environment';
import { CareProfile } from '../models/care-profile.model';

describe('CareProfileService', () => {
  let service: CareProfileService;
  let httpMock: HttpTestingController;

  const apiUrl = `${environment.apiUrl}/plants`;

  const careProfile: CareProfile = {
    id: 1, plantId: 5, lightRequirement: 'hell', temperatureMin: 15, temperatureMax: 25,
    humidityRequirement: 'mittel', wateringIntervalDays: 7, fertilizingIntervalDays: 30, notes: null,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(CareProfileService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('get sends a GET to /plants/:plantId/care-profile', () => {
    let result: CareProfile | undefined;

    service.get(5).subscribe((res) => (result = res));

    const req = httpMock.expectOne(`${apiUrl}/5/care-profile`);
    expect(req.request.method).toBe('GET');
    req.flush(careProfile);

    expect(result).toEqual(careProfile);
  });

  it('update sends a PUT to /plants/:plantId/care-profile with the request body', () => {
    const request = {
      lightRequirement: 'hell', temperatureMin: 15, temperatureMax: 25,
      humidityRequirement: 'mittel', wateringIntervalDays: 7, fertilizingIntervalDays: 30, notes: null,
    };

    service.update(5, request).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/5/care-profile`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);
    req.flush(careProfile);
  });
});
