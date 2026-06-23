package com.bagin.plantcare.rest.dto;

import com.bagin.plantcare.domain.model.PlantSpecies;

public record PlantSpeciesResponse(
    Long id,
    String commonName,
    String scientificName,
    String defaultWatering,
    String defaultSunlight,
    String imageUrl
) {
  public static PlantSpeciesResponse fromDomain(PlantSpecies species) {
    return new PlantSpeciesResponse(
        species.getId(),
        species.getCommonName(),
        species.getScientificName(),
        species.getDefaultWatering(),
        species.getDefaultSunlight(),
        species.getImageUrl()
    );
  }
}