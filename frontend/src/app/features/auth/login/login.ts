import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  email = '';
  password = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
  }

  onSubmit(): void {
    this.errorMessage = '';

    this.authService.login({email: this.email, password: this.password}).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);
        this.router.navigate(['/plants']);
      },
      error: (err) => {
        this.errorMessage = 'Login fehlgeschlagen. Bitte prüfe deine Zugangsdaten.';
      }
    });
  }
}
