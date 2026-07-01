import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { PushNotificationService } from '../../../core/services/push-notification.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  email = '';
  password = '';
  errorMessage = signal('');

  constructor(
    private authService: AuthService,
    private pushNotificationService: PushNotificationService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage.set('');

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);
        this.pushNotificationService.subscribeToPush();
        this.router.navigate(['/plants']);
      },
      error: (err) => {
        this.errorMessage.set('Login fehlgeschlagen. Bitte prüfe deine Zugangsdaten.');
      }
    });
  }
}
