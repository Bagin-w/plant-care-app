package com.bagin.plantcare.persistence.mapper;

import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.persistence.tablemodel.PlantEntity;
import org.springframework.stereotype.Component;

@Component
public class PlantMapper {

  public Plant toDomain(PlantEntity entity) {
    return new Plant(
        entity.getId(),
        entity.getUserId(),
        entity.getNickname(),
        entity.getSpeciesName(),
        entity.getPhotoUrl(),
        entity.getLocation()
    );
  }

  public PlantEntity toEntity(Plant plant) {
    PlantEntity entity = new PlantEntity();
    entity.setId(plant.getId());
    entity.setUserId(plant.getUserId());
    entity.setNickname(plant.getNickname());
    entity.setSpeciesName(plant.getSpeciesName());
    entity.setPhotoUrl(plant.getPhotoUrl());
    entity.setLocation(plant.getLocation());
    return entity;
  }
}