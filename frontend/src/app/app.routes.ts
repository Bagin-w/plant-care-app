import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { PlantList } from './features/plants/plant-list/plant-list';
import { PlantForm } from './features/plants/plant-form/plant-form';
import { PlantDetail } from './features/plants/plant-detail/plant-detail';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'plants', component: PlantList, canActivate: [authGuard] },
  { path: 'plants/new', component: PlantForm, canActivate: [authGuard] },
  { path: 'plants/:id', component: PlantDetail, canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
