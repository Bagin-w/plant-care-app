import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {

  name = '';
  email = '';
  password = '';
  errorMessage = signal('');

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage.set('');

    this.authService.register({ name: this.name, email: this.email, password: this.password }).subscribe({
      next: () => {
        this.authService.login({ email: this.email, password: this.password }).subscribe({
          next: (response) => {
            this.authService.saveToken(response.token);
            this.router.navigate(['/plants']);
          },
          error: () => {
            this.router.navigate(['/login']);
          }
        });
      },
      error: (err) => {
        if (err.status === 409) {
          this.errorMessage.set('Diese E-Mail-Adresse ist bereits registriert.');
        } else {
          this.errorMessage.set('Registrierung fehlgeschlagen. Bitte versuche es erneut.');
        }
      }
    });
  }
}
