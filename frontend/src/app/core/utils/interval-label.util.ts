export function formatIntervalDays(days: number): string {
  return days === 1 ? 'jeden Tag' : `alle ${days} Tage`;
}
