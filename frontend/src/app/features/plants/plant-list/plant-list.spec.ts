import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { PlantList } from './plant-list';
import { PlantService } from '../../../core/services/plant.service';
import { Plant } from '../../../core/models/plant.model';

describe('PlantList', () => {
  let component: PlantList;
  let fixture: ComponentFixture<PlantList>;
  let plantServiceMock: { getAll: ReturnType<typeof vi.fn>; delete: ReturnType<typeof vi.fn> };

  const plants: Plant[] = [
    { id: 1, nickname: 'Ficus', speciesName: 'Ficus lyrata', photoUrl: null, location: 'Wohnzimmer' },
    { id: 2, nickname: 'Kaktus', speciesName: 'Cactaceae', photoUrl: null, location: 'Balkon' },
  ];

  beforeEach(async () => {
    plantServiceMock = { getAll: vi.fn().mockReturnValue(of([])), delete: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [PlantList],
      providers: [
        provideRouter([]),
        { provide: PlantService, useValue: plantServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PlantList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loadPlants populates the plants signal on success', () => {
    plantServiceMock.getAll.mockReturnValue(of(plants));

    component.loadPlants();

    expect(component.plants()).toEqual(plants);
    expect(component.errorMessage()).toBe('');
  });

  it('loadPlants sets an error message on failure', () => {
    plantServiceMock.getAll.mockReturnValue(throwError(() => new Error('network error')));

    component.loadPlants();

    expect(component.errorMessage()).toBe('Pflanzen konnten nicht geladen werden.');
  });

  it('onDelete does nothing when the confirmation is cancelled', () => {
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(false);
    component.plants.set(plants);

    component.onDelete(1);

    expect(plantServiceMock.delete).not.toHaveBeenCalled();
    expect(component.plants()).toEqual(plants);
    confirmSpy.mockRestore();
  });

  it('onDelete removes the plant from the list on success', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    plantServiceMock.delete.mockReturnValue(of(undefined));
    component.plants.set(plants);

    component.onDelete(1);

    expect(plantServiceMock.delete).toHaveBeenCalledWith(1);
    expect(component.plants()).toEqual([plants[1]]);
  });

  it('onDelete sets an error message on failure', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    plantServiceMock.delete.mockReturnValue(throwError(() => new Error('server error')));
    component.plants.set(plants);

    component.onDelete(1);

    expect(component.errorMessage()).toBe('Pflanze konnte nicht gelöscht werden.');
    expect(component.plants()).toEqual(plants);
  });
});
