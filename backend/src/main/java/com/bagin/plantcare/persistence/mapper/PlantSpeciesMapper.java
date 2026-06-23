package com.bagin.plantcare.persistence.mapper;

import com.bagin.plantcare.domain.model.PlantSpecies;
import com.bagin.plantcare.persistence.tablemodel.PlantSpeciesEntity;
import org.springframework.stereotype.Component;

@Component
public class PlantSpeciesMapper {

  public PlantSpecies toDomain(PlantSpeciesEntity entity) {
    return new PlantSpecies(
        entity.getId(),
        entity.getPerenualId(),
        entity.getCommonName(),
        entity.getScientificName(),
        entity.getDefaultWatering(),
        entity.getDefaultSunlight(),
        entity.getImageUrl()
    );
  }

  public PlantSpeciesEntity toEntity(PlantSpecies species) {
    PlantSpeciesEntity entity = new PlantSpeciesEntity();
    entity.setId(species.getId());
    entity.setPerenualId(species.getPerenualId());
    entity.setCommonName(species.getCommonName());
    entity.setScientificName(species.getScientificName());
    entity.setDefaultWatering(species.getDefaultWatering());
    entity.setDefaultSunlight(species.getDefaultSunlight());
    entity.setImageUrl(species.getImageUrl());
    return entity;
  }
}