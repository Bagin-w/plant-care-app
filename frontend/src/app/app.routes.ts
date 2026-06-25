import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { PlantList } from './features/plants/plant-list/plant-list';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'plants', component: PlantList },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
