import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { PlantService } from '../../../core/services/plant.service';
import { CareProfileService } from '../../../core/services/care-profile.service';
import { Plant } from '../../../core/models/plant.model';
import { CareProfile } from '../../../core/models/care-profile.model';

interface CareProfileFormState {
  lightRequirement: string;
  temperatureMin: number | null;
  temperatureMax: number | null;
  humidityRequirement: string;
  wateringIntervalDays: number | null;
  fertilizingIntervalDays: number | null;
  notes: string;
}

@Component({
  selector: 'app-plant-detail',
  imports: [FormsModule],
  templateUrl: './plant-detail.html',
  styleUrl: './plant-detail.css'
})
export class PlantDetail implements OnInit {

  plant = signal<Plant | null>(null);
  errorMessage = signal('');
  successMessage = signal('');

  plantId!: number;

  form = signal<CareProfileFormState>({
    lightRequirement: '',
    temperatureMin: null,
    temperatureMax: null,
    humidityRequirement: '',
    wateringIntervalDays: null,
    fertilizingIntervalDays: null,
    notes: ''
  });

  constructor(
    private route: ActivatedRoute,
    private plantService: PlantService,
    private careProfileService: CareProfileService
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
      }
    });

    this.careProfileService.get(this.plantId).subscribe({
      next: (profile: CareProfile) => {
        this.form.set({
          lightRequirement: profile.lightRequirement ?? '',
          temperatureMin: profile.temperatureMin,
          temperatureMax: profile.temperatureMax,
          humidityRequirement: profile.humidityRequirement ?? '',
          wateringIntervalDays: profile.wateringIntervalDays,
          fertilizingIntervalDays: profile.fertilizingIntervalDays,
          notes: profile.notes ?? ''
        });
      },
      error: (err) => {
        this.errorMessage.set('Pflegeprofil konnte nicht geladen werden.');
      }
    });
  }

  updateField<K extends keyof CareProfileFormState>(field: K, value: CareProfileFormState[K]): void {
    this.form.update(current => ({ ...current, [field]: value }));
  }

  onSave(): void {
    this.successMessage.set('');
    this.errorMessage.set('');

    const current = this.form();

    this.careProfileService.update(this.plantId, {
      lightRequirement: current.lightRequirement || null,
      temperatureMin: current.temperatureMin,
      temperatureMax: current.temperatureMax,
      humidityRequirement: current.humidityRequirement || null,
      wateringIntervalDays: current.wateringIntervalDays,
      fertilizingIntervalDays: current.fertilizingIntervalDays,
      notes: current.notes || null
    }).subscribe({
      next: () => {
        this.successMessage.set('Gespeichert!');
      },
      error: (err) => {
        this.errorMessage.set('Speichern fehlgeschlagen.');
      }
    });
  }
}
