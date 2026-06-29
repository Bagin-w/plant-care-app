import { Component, OnInit, signal } from '@angular/core';
import { PlantService } from '../../../core/services/plant.service';
import { Plant } from '../../../core/models/plant.model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-plant-list',
  imports: [RouterLink],
  templateUrl: './plant-list.html',
  styleUrl: './plant-list.css'
})
export class PlantList implements OnInit {

  plants = signal<Plant[]>([]);
  errorMessage = signal('');

  constructor(private plantService: PlantService) {}

  ngOnInit(): void {
    this.loadPlants();
  }

  loadPlants(): void {
    this.plantService.getAll().subscribe({
      next: (data) => {
        this.plants.set(data);
      },
      error: (err) => {
        this.errorMessage.set('Pflanzen konnten nicht geladen werden.');
      }
    });
  }

  onDelete(id: number): void {
    if (!confirm('Diese Pflanze wirklich löschen?')) {
      return;
    }

    this.plantService.delete(id).subscribe({
      next: () => {
        this.plants.update(current => current.filter(p => p.id !== id));
      },
      error: (err) => {
        this.errorMessage.set('Pflanze konnte nicht gelöscht werden.');
      }
    });
  }
}
