package com.bagin.plantcare.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CareProfile {

  private final Long id;
  private final Long plantId;
  private final String lightRequirement;
  private final Integer temperatureMin;
  private final Integer temperatureMax;
  private final String humidityRequirement;
  private final Integer wateringIntervalDays;
  private final Integer fertilizingIntervalDays;
  private final String notes;
}