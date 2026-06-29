import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReminderRule, CreateReminderRequest } from '../models/reminder.model';

@Injectable({
  providedIn: 'root'
})
export class ReminderService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getAllForPlant(plantId: number): Observable<ReminderRule[]> {
    return this.http.get<ReminderRule[]>(`${this.apiUrl}/plants/${plantId}/reminders`);
  }

  create(plantId: number, request: CreateReminderRequest): Observable<ReminderRule> {
    return this.http.post<ReminderRule>(`${this.apiUrl}/plants/${plantId}/reminders`, request);
  }

  deactivate(reminderId: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/reminders/${reminderId}/deactivate`, {});
  }

  activate(reminderId: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/reminders/${reminderId}/activate`, {});
  }

  delete(reminderId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/reminders/${reminderId}`);
  }
}
