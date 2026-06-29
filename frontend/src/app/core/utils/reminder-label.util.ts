import { ReminderType } from '../models/reminder.model';

export function getReminderTypeLabel(type: ReminderType, customLabel: string | null): string {
  switch (type) {
    case 'WATERING':
      return 'Gießen';
    case 'FERTILIZING':
      return 'Düngen';
    case 'CUSTOM':
      return customLabel ?? 'Sonstiges';
  }
}
