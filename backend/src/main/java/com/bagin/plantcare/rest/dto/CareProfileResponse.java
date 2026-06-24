package com.bagin.plantcare.rest.dto;

import com.bagin.plantcare.domain.model.CareProfile;

public record CareProfileResponse(
    Long id,
    Long plantId,
    String lightRequirement,
    Integer temperatureMin,
    Integer temperatureMax,
    String humidityRequirement,
    Integer wateringIntervalDays,
    Integer fertilizingIntervalDays,
    String notes
) {
  public static CareProfileResponse fromDomain(CareProfile careProfile) {
    return new CareProfileResponse(
        careProfile.getId(),
        careProfile.getPlantId(),
        careProfile.getLightRequirement(),
        careProfile.getTemperatureMin(),
        careProfile.getTemperatureMax(),
        careProfile.getHumidityRequirement(),
        careProfile.getWateringIntervalDays(),
        careProfile.getFertilizingIntervalDays(),
        careProfile.getNotes()
    );
  }
}