package com.bagin.plantcare.ports.in;

import com.bagin.plantcare.domain.model.CareProfile;

public interface CareProfileUseCase {

  CareProfile getByPlantId(Long userId, Long plantId);

  CareProfile updateCareProfile(
      Long userId,
      Long plantId,
      String lightRequirement,
      Integer temperatureMin,
      Integer temperatureMax,
      String humidityRequirement,
      Integer wateringIntervalDays,
      Integer fertilizingIntervalDays,
      String notes
  );
}