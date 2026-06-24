package com.bagin.plantcare.persistence.mapper;

import com.bagin.plantcare.domain.model.CareProfile;
import com.bagin.plantcare.persistence.tablemodel.CareProfileEntity;
import org.springframework.stereotype.Component;

@Component
public class CareProfileMapper {

  public CareProfile toDomain(CareProfileEntity entity) {
    return new CareProfile(
        entity.getId(),
        entity.getPlantId(),
        entity.getLightRequirement(),
        entity.getTemperatureMin(),
        entity.getTemperatureMax(),
        entity.getHumidityRequirement(),
        entity.getWateringIntervalDays(),
        entity.getFertilizingIntervalDays(),
        entity.getNotes()
    );
  }

  public CareProfileEntity toEntity(CareProfile careProfile) {
    CareProfileEntity entity = new CareProfileEntity();
    entity.setId(careProfile.getId());
    entity.setPlantId(careProfile.getPlantId());
    entity.setLightRequirement(careProfile.getLightRequirement());
    entity.setTemperatureMin(careProfile.getTemperatureMin());
    entity.setTemperatureMax(careProfile.getTemperatureMax());
    entity.setHumidityRequirement(careProfile.getHumidityRequirement());
    entity.setWateringIntervalDays(careProfile.getWateringIntervalDays());
    entity.setFertilizingIntervalDays(careProfile.getFertilizingIntervalDays());
    entity.setNotes(careProfile.getNotes());
    return entity;
  }
}