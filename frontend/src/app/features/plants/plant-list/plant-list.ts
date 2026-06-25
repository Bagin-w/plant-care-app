import { Component, OnInit, signal } from '@angular/core';
import { PlantService } from '../../../core/services/plant.service';
import { Plant } from '../../../core/models/plant.model';

@Component({
  selector: 'app-plant-list',
  imports: [],
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
}
