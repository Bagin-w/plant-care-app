import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { PlantService } from '../../../core/services/plant.service';
import { CareProfileService } from '../../../core/services/care-profile.service';
import { ReminderService } from '../../../core/services/reminder.service';
import { Plant } from '../../../core/models/plant.model';
import { CareProfile } from '../../../core/models/care-profile.model';
import { ReminderRule, ReminderType } from '../../../core/models/reminder.model';
import { getReminderTypeLabel } from '../../../core/utils/reminder-label.util';
import {DatePipe, SlicePipe} from '@angular/common';
import { formatIntervalDays } from '../../../core/utils/interval-label.util';


interface PlantFormState {
  nickname: string;
  speciesName: string;
  location: string;
}

interface CareProfileFormState {
  lightRequirement: string;
  temperatureMin: number | null;
  temperatureMax: number | null;
  humidityRequirement: string;
  wateringIntervalDays: number | null;
  fertilizingIntervalDays: number | null;
  notes: string;
}

interface NewReminderFormState {
  type: ReminderType;
  customLabel: string;
  intervalDays: number;
  preferredTime: string;
}

@Component({
  selector: 'app-plant-detail',
  imports: [FormsModule, DatePipe, SlicePipe],
  templateUrl: './plant-detail.html',
  styleUrl: './plant-detail.css'
})
export class PlantDetail implements OnInit {

  plant = signal<Plant | null>(null);
  careProfile = signal<CareProfile | null>(null);
  reminders = signal<ReminderRule[]>([]);
  errorMessage = signal('');
  successMessage = signal('');
  isEditMode = signal(false);
  showReminderForm = signal(false);

  plantId!: number;

  plantForm = signal<PlantFormState>({
    nickname: '',
    speciesName: '',
    location: ''
  });

  careProfileForm = signal<CareProfileFormState>({
    lightRequirement: '',
    temperatureMin: null,
    temperatureMax: null,
    humidityRequirement: '',
    wateringIntervalDays: null,
    fertilizingIntervalDays: null,
    notes: ''
  });

  newReminderForm = signal<NewReminderFormState>({
    type: 'WATERING',
    customLabel: '',
    intervalDays: 7,
    preferredTime: ''
  });

  constructor(
    private route: ActivatedRoute,
    private plantService: PlantService,
    private careProfileService: CareProfileService,
    private reminderService: ReminderService
  ) {}

  ngOnInit(): void {
    this.plantId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadData();
  }

  loadData(): void {
    this.plantService.getAll().subscribe({
      next: (plants) => {
        const found = plants.find(p => p.id === this.plantId) ?? null;
        this.plant.set(found);
        if (found) {
          this.plantForm.set({
            nickname: found.nickname,
            speciesName: found.speciesName,
            location: found.location
          });
        }
      }
    });

    this.careProfileService.get(this.plantId).subscribe({
      next: (profile) => {
        this.careProfile.set(profile);
        this.careProfileForm.set({
          lightRequirement: profile.lightRequirement ?? '',
          temperatureMin: profile.temperatureMin,
          temperatureMax: profile.temperatureMax,
          humidityRequirement: profile.humidityRequirement ?? '',
          wateringIntervalDays: profile.wateringIntervalDays,
          fertilizingIntervalDays: profile.fertilizingIntervalDays,
          notes: profile.notes ?? ''
        });
      },
      error: () => {
        this.errorMessage.set('Pflegeprofil konnte nicht geladen werden.');
      }
    });

    this.loadReminders();
  }

  loadReminders(): void {
    this.reminderService.getAllForPlant(this.plantId).subscribe({
      next: (data) => {
        this.reminders.set(data);
      },
      error: () => {
        this.errorMessage.set('Erinnerungen konnten nicht geladen werden.');
      }
    });
  }

  updatePlantField<K extends keyof PlantFormState>(field: K, value: PlantFormState[K]): void {
    this.plantForm.update(current => ({ ...current, [field]: value }));
  }

  updateCareProfileField<K extends keyof CareProfileFormState>(field: K, value: CareProfileFormState[K]): void {
    this.careProfileForm.update(current => ({ ...current, [field]: value }));
  }

  updateNewReminderField<K extends keyof NewReminderFormState>(field: K, value: NewReminderFormState[K]): void {
    this.newReminderForm.update(current => ({ ...current, [field]: value }));
  }

  enterEditMode(): void {
    this.isEditMode.set(true);
    this.successMessage.set('');
  }

  cancelEdit(): void {
    this.isEditMode.set(false);
    this.loadData();
  }

  onSave(): void {
    this.successMessage.set('');
    this.errorMessage.set('');

    const plantData = this.plantForm();
    const careProfileData = this.careProfileForm();

    this.plantService.update(this.plantId, {
      nickname: plantData.nickname,
      speciesName: plantData.speciesName,
      photoUrl: this.plant()?.photoUrl ?? null,
      location: plantData.location
    }).subscribe({
      next: (updatedPlant) => {
        this.plant.set(updatedPlant);

        this.careProfileService.update(this.plantId, {
          lightRequirement: careProfileData.lightRequirement || null,
          temperatureMin: careProfileData.temperatureMin,
          temperatureMax: careProfileData.temperatureMax,
          humidityRequirement: careProfileData.humidityRequirement || null,
          wateringIntervalDays: careProfileData.wateringIntervalDays,
          fertilizingIntervalDays: careProfileData.fertilizingIntervalDays,
          notes: careProfileData.notes || null
        }).subscribe({
          next: (updatedProfile) => {
            this.careProfile.set(updatedProfile);
            this.successMessage.set('Gespeichert!');
            this.isEditMode.set(false);
          },
          error: () => {
            this.errorMessage.set('Pflegeprofil konnte nicht gespeichert werden.');
          }
        });
      },
      error: () => {
        this.errorMessage.set('Pflanzendaten konnten nicht gespeichert werden.');
      }
    });
  }

  toggleReminderForm(): void {
    this.showReminderForm.update(current => !current);
  }

  onCreateReminder(): void {
    const data = this.newReminderForm();

    this.reminderService.create(this.plantId, {
      type: data.type,
      customLabel: data.type === 'CUSTOM' ? data.customLabel || null : null,
      intervalDays: data.intervalDays,
      preferredTime: data.preferredTime || null
    }).subscribe({
      next: () => {
        this.loadReminders();
        this.showReminderForm.set(false);
        this.newReminderForm.set({ type: 'WATERING', customLabel: '', intervalDays: 7, preferredTime: '' });
      },
      error: () => {
        this.errorMessage.set('Erinnerung konnte nicht angelegt werden.');
      }
    });
  }

  onDeactivateReminder(reminderId: number): void {
    this.reminderService.deactivate(reminderId).subscribe({
      next: () => this.loadReminders(),
      error: () => this.errorMessage.set('Erinnerung konnte nicht deaktiviert werden.')
    });
  }

  onActivateReminder(reminderId: number): void {
    this.reminderService.activate(reminderId).subscribe({
      next: () => this.loadReminders(),
      error: () => this.errorMessage.set('Erinnerung konnte nicht reaktiviert werden.')
    });
  }

  onDeleteReminder(reminderId: number): void {
    if (!confirm('Diese Erinnerung wirklich löschen?')) {
      return;
    }
    this.reminderService.delete(reminderId).subscribe({
      next: () => this.loadReminders(),
      error: () => this.errorMessage.set('Erinnerung konnte nicht gelöscht werden.')
    });
  }

  getTypeLabel(type: ReminderType, customLabel: string | null): string {
    return getReminderTypeLabel(type, customLabel);
  }

  getIntervalLabel(days: number): string {
    return formatIntervalDays(days);
  }
}
