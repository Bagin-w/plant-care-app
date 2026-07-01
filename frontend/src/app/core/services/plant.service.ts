import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Plant, CreatePlantRequest } from '../models/plant.model';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PlantService {

  private apiUrl = `${environment.apiUrl}/plants`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Plant[]> {
    return this.http.get<Plant[]>(this.apiUrl);
  }

  create(request: CreatePlantRequest): Observable<Plant> {
    return this.http.post<Plant>(this.apiUrl, request);
  }

  update(id: number, request: CreatePlantRequest): Observable<Plant> {
    return this.http.put<Plant>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
