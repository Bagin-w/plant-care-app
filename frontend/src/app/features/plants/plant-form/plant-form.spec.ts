import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { PlantForm } from './plant-form';
import { PlantService } from '../../../core/services/plant.service';
import { Plant } from '../../../core/models/plant.model';

describe('PlantForm', () => {
  let component: PlantForm;
  let fixture: ComponentFixture<PlantForm>;
  let plantServiceMock: { create: ReturnType<typeof vi.fn> };
  let navigateSpy: ReturnType<typeof vi.spyOn>;

  beforeEach(async () => {
    plantServiceMock = { create: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [PlantForm],
      providers: [
        provideRouter([]),
        { provide: PlantService, useValue: plantServiceMock },
      ],
    }).compileComponents();

    navigateSpy = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(PlantForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows an error and does not call the service when the nickname is blank', () => {
    component.nickname = '   ';

    component.onSubmit();

    expect(component.errorMessage()).toBe('Bitte gib deiner Pflanze einen Namen.');
    expect(plantServiceMock.create).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('creates the plant with trimmed values and navigates to /plants on success', () => {
    const created: Plant = { id: 1, nickname: 'Ficus', speciesName: 'Ficus lyrata', photoUrl: null, location: 'Wohnzimmer' };
    plantServiceMock.create.mockReturnValue(of(created));
    component.nickname = '  Ficus  ';
    component.speciesName = '  Ficus lyrata  ';
    component.location = '  Wohnzimmer  ';

    component.onSubmit();

    expect(plantServiceMock.create).toHaveBeenCalledWith({
      nickname: 'Ficus',
      speciesName: 'Ficus lyrata',
      photoUrl: null,
      location: 'Wohnzimmer',
    });
    expect(navigateSpy).toHaveBeenCalledWith(['/plants']);
    expect(component.errorMessage()).toBe('');
  });

  it('sets an error message and does not navigate when creation fails', () => {
    plantServiceMock.create.mockReturnValue(throwError(() => new Error('server error')));
    component.nickname = 'Ficus';

    component.onSubmit();

    expect(component.errorMessage()).toBe('Pflanze konnte nicht angelegt werden.');
    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
