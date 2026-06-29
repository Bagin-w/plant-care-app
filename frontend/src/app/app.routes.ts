import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { PlantList } from './features/plants/plant-list/plant-list';
import { PlantForm } from './features/plants/plant-form/plant-form';
import { PlantDetail } from './features/plants/plant-detail/plant-detail';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'plants', component: PlantList },
  { path: 'plants/new', component: PlantForm },
  { path: 'plants/:id', component: PlantDetail },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
