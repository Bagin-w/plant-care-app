package com.bagin.plantcare.rest.dto;

public record UpdateCareProfileRequest(
    String lightRequirement,
    Integer temperatureMin,
    Integer temperatureMax,
    String humidityRequirement,
    Integer wateringIntervalDays,
    Integer fertilizingIntervalDays,
    String notes
) {
}