import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { PlantService } from './plant.service';
import { environment } from '../../../environments/environment';
import { Plant } from '../models/plant.model';

describe('PlantService', () => {
  let service: PlantService;
  let httpMock: HttpTestingController;

  const apiUrl = `${environment.apiUrl}/plants`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(PlantService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getAll sends a GET to /plants and returns the list', () => {
    const plants: Plant[] = [{ id: 1, nickname: 'Ficus', speciesName: 'Ficus lyrata', photoUrl: null, location: 'Wohnzimmer' }];
    let result: Plant[] | undefined;

    service.getAll().subscribe((res) => (result = res));

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(plants);

    expect(result).toEqual(plants);
  });

  it('create sends a POST with the request body to /plants', () => {
    const request = { nickname: 'Ficus', speciesName: 'Ficus lyrata', photoUrl: null, location: 'Wohnzimmer' };

    service.create(request).subscribe();

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush({ id: 1, ...request });
  });

  it('update sends a PUT to /plants/:id with the request body', () => {
    const request = { nickname: 'Ficus', speciesName: 'Ficus lyrata', photoUrl: null, location: 'Wohnzimmer' };

    service.update(5, request).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/5`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);
    req.flush({ id: 5, ...request });
  });

  it('delete sends a DELETE to /plants/:id', () => {
    service.delete(5).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/5`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
