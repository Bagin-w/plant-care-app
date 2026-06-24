package com.bagin.plantcare.rest.dto;

import com.bagin.plantcare.domain.model.Plant;

public record PlantResponse(
    Long id,
    String nickname,
    String speciesName,
    String photoUrl,
    String location
) {
  public static PlantResponse fromDomain(Plant plant) {
    return new PlantResponse(
        plant.getId(),
        plant.getNickname(),
        plant.getSpeciesName(),
        plant.getPhotoUrl(),
        plant.getLocation()
    );
  }
}