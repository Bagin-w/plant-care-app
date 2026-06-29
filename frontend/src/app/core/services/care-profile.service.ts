import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CareProfile, UpdateCareProfileRequest } from '../models/care-profile.model';

@Injectable({
  providedIn: 'root'
})
export class CareProfileService {

  private apiUrl = 'http://localhost:8080/api/plants';

  constructor(private http: HttpClient) {}

  get(plantId: number): Observable<CareProfile> {
    return this.http.get<CareProfile>(`${this.apiUrl}/${plantId}/care-profile`);
  }

  update(plantId: number, request: UpdateCareProfileRequest): Observable<CareProfile> {
    return this.http.put<CareProfile>(`${this.apiUrl}/${plantId}/care-profile`, request);
  }
}
