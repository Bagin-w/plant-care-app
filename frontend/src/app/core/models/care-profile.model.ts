export interface CareProfile {
  id: number;
  plantId: number;
  lightRequirement: string | null;
  temperatureMin: number | null;
  temperatureMax: number | null;
  humidityRequirement: string | null;
  wateringIntervalDays: number | null;
  fertilizingIntervalDays: number | null;
  notes: string | null;
}

export interface UpdateCareProfileRequest {
  lightRequirement: string | null;
  temperatureMin: number | null;
  temperatureMax: number | null;
  humidityRequirement: string | null;
  wateringIntervalDays: number | null;
  fertilizingIntervalDays: number | null;
  notes: string | null;
}
