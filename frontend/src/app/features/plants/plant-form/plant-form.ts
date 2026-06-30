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

    this.plantService.create({
      nickname: this.nickname,
      speciesName: this.speciesName,
      photoUrl: null,
      location: this.location
    }).subscribe({
      next: () => {
        this.router.navigate(['/plants']);
      },
      error: (err) => {
        this.errorMessage.set('Pflanze konnte nicht angelegt werden.');
      }
    });
  }
}
