package com.bagin.plantcare.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlantSpecies {

  private final Long id;
  private final String commonName;
  private final String scientificName;
  private final String defaultWatering;
  private final String defaultSunlight;
  private final String imageUrl;

}
