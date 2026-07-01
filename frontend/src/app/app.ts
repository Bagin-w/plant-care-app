import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Navigation } from './core/navigation/navigation';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navigation],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  constructor(public router: Router) {}

  showNavigation(): boolean {
    const hideOn = ['/login', '/register'];
    return !hideOn.includes(this.router.url);
  }
}
