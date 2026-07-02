import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { PlantDetail } from './plant-detail';
import { PlantService } from '../../../core/services/plant.service';
import { CareProfileService } from '../../../core/services/care-profile.service';
import { ReminderService } from '../../../core/services/reminder.service';
import { Plant } from '../../../core/models/plant.model';
import { CareProfile } from '../../../core/models/care-profile.model';
import { ReminderRule } from '../../../core/models/reminder.model';

describe('PlantDetail', () => {
  let component: PlantDetail;
  let fixture: ComponentFixture<PlantDetail>;
  let plantServiceMock: { getAll: ReturnType<typeof vi.fn>; update: ReturnType<typeof vi.fn> };
  let careProfileServiceMock: { get: ReturnType<typeof vi.fn>; update: ReturnType<typeof vi.fn> };
  let reminderServiceMock: {
    getAllForPlant: ReturnType<typeof vi.fn>;
    create: ReturnType<typeof vi.fn>;
    deactivate: ReturnType<typeof vi.fn>;
    activate: ReturnType<typeof vi.fn>;
    delete: ReturnType<typeof vi.fn>;
  };

  const plant: Plant = { id: 1, nickname: 'Ficus', speciesName: 'Ficus lyrata', photoUrl: null, location: 'Wohnzimmer' };
  const careProfile: CareProfile = {
    id: 1,
    plantId: 1,
    lightRequirement: 'hell',
    temperatureMin: 15,
    temperatureMax: 25,
    humidityRequirement: 'mittel',
    wateringIntervalDays: 7,
    fertilizingIntervalDays: 30,
    notes: null,
  };

  beforeEach(async () => {
    plantServiceMock = { getAll: vi.fn().mockReturnValue(of([plant])), update: vi.fn() };
    careProfileServiceMock = { get: vi.fn().mockReturnValue(of(careProfile)), update: vi.fn() };
    reminderServiceMock = {
      getAllForPlant: vi.fn().mockReturnValue(of([])),
      create: vi.fn(),
      deactivate: vi.fn(),
      activate: vi.fn(),
      delete: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [PlantDetail],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } },
        },
        { provide: PlantService, useValue: plantServiceMock },
        { provide: CareProfileService, useValue: careProfileServiceMock },
        { provide: ReminderService, useValue: reminderServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PlantDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loads the matching plant, care profile, and reminders on init', () => {
    expect(component.plant()).toEqual(plant);
    expect(component.plantForm()).toEqual({
      nickname: 'Ficus',
      speciesName: 'Ficus lyrata',
      location: 'Wohnzimmer',
    });
    expect(component.careProfile()).toEqual(careProfile);
    expect(component.careProfileForm()).toEqual({
      lightRequirement: 'hell',
      temperatureMin: 15,
      temperatureMax: 25,
      humidityRequirement: 'mittel',
      wateringIntervalDays: 7,
      fertilizingIntervalDays: 30,
      notes: '',
    });
    expect(component.reminders()).toEqual([]);
  });

  it('shows an error and does not call the service when saving a blank nickname', () => {
    component.updatePlantField('nickname', '   ');

    component.onSave();

    expect(component.errorMessage()).toBe('Der Name der Pflanze darf nicht leer sein.');
    expect(plantServiceMock.update).not.toHaveBeenCalled();
  });

  it('saves the plant and care profile, shows a success message, and exits edit mode', () => {
    const updatedPlant = { ...plant, nickname: 'Ficus (neu)' };
    plantServiceMock.update.mockReturnValue(of(updatedPlant));
    careProfileServiceMock.update.mockReturnValue(of(careProfile));
    component.enterEditMode();
    component.updatePlantField('nickname', 'Ficus (neu)');

    component.onSave();

    expect(plantServiceMock.update).toHaveBeenCalledWith(1, {
      nickname: 'Ficus (neu)',
      speciesName: 'Ficus lyrata',
      photoUrl: null,
      location: 'Wohnzimmer',
    });
    expect(component.plant()).toEqual(updatedPlant);
    expect(component.successMessage()).toBe('Gespeichert!');
    expect(component.isEditMode()).toBe(false);
  });

  it('shows an error message when saving the plant fails', () => {
    plantServiceMock.update.mockReturnValue(throwError(() => new Error('server error')));

    component.onSave();

    expect(component.errorMessage()).toBe('Pflanzendaten konnten nicht gespeichert werden.');
  });

  it('shows an error and does not create a reminder when preferredTime is missing', () => {
    component.onCreateReminder();

    expect(component.errorMessage()).toBe('Bitte gib eine Uhrzeit für die Erinnerung an.');
    expect(reminderServiceMock.create).not.toHaveBeenCalled();
  });

  it('creates a reminder, reloads the list, and resets the form on success', () => {
    reminderServiceMock.create.mockReturnValue(of({} as ReminderRule));
    component.toggleReminderForm();
    component.updateNewReminderField('preferredTime', '09:00');
    component.updateNewReminderField('intervalDays', 5);

    component.onCreateReminder();

    expect(reminderServiceMock.create).toHaveBeenCalledWith(1, {
      type: 'WATERING',
      customLabel: null,
      intervalDays: 5,
      preferredTime: '09:00',
    });
    expect(reminderServiceMock.getAllForPlant).toHaveBeenCalledTimes(2);
    expect(component.showReminderForm()).toBe(false);
    expect(component.newReminderForm()).toEqual({
      type: 'WATERING',
      customLabel: '',
      intervalDays: 7,
      preferredTime: '',
    });
  });

  it('deactivates a reminder and reloads the list', () => {
    reminderServiceMock.deactivate.mockReturnValue(of(undefined));

    component.onDeactivateReminder(2);

    expect(reminderServiceMock.deactivate).toHaveBeenCalledWith(2);
    expect(reminderServiceMock.getAllForPlant).toHaveBeenCalledTimes(2);
  });

  it('activates a reminder and reloads the list', () => {
    reminderServiceMock.activate.mockReturnValue(of(undefined));

    component.onActivateReminder(2);

    expect(reminderServiceMock.activate).toHaveBeenCalledWith(2);
    expect(reminderServiceMock.getAllForPlant).toHaveBeenCalledTimes(2);
  });

  it('deletes a reminder only after confirmation', () => {
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(false);

    component.onDeleteReminder(2);

    expect(reminderServiceMock.delete).not.toHaveBeenCalled();
    confirmSpy.mockRestore();
  });

  it('deletes the reminder and reloads the list when confirmed', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    reminderServiceMock.delete.mockReturnValue(of(undefined));

    component.onDeleteReminder(2);

    expect(reminderServiceMock.delete).toHaveBeenCalledWith(2);
    expect(reminderServiceMock.getAllForPlant).toHaveBeenCalledTimes(2);
  });
});
