export type ReminderType = 'WATERING' | 'FERTILIZING' | 'CUSTOM';

export interface ReminderRule {
  id: number;
  plantId: number;
  type: ReminderType;
  customLabel: string | null;
  intervalDays: number;
  preferredTime: string | null;
  lastTriggeredAt: string | null;
  nextDueAt: string;
  active: boolean;
}

export interface CreateReminderRequest {
  type: ReminderType;
  customLabel: string | null;
  intervalDays: number;
  preferredTime: string | null;
}
