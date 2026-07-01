import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PlantService } from '../../../core/services/plant.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-plant-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './plant-form.html',
  styleUrl: './plant-form.css'
})
export class PlantForm {

  nickname = '';
  speciesName = '';
  location = '';
  errorMessage = signal('');

  constructor(
    private plantService: PlantService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage.set('');

    if (!this.nickname.trim()) {
      this.errorMessage.set('Bitte gib deiner Pflanze einen Namen.');
      return;
    }

    this.plantService.create({
      nickname: this.nickname.trim(),
      speciesName: this.speciesName.trim(),
      photoUrl: null,
      location: this.location.trim()
    }).subscribe({
      next: () => {
        this.router.navigate(['/plants']);
      },
      error: () => {
        this.errorMessage.set('Pflanze konnte nicht angelegt werden.');
      }
    });
  }
}
