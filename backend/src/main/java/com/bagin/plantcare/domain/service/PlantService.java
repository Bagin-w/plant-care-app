package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.CareProfile;
import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.ports.in.PlantUseCase;
import com.bagin.plantcare.ports.out.CareProfilePort;
import com.bagin.plantcare.ports.out.PlantPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantService implements PlantUseCase {

  private final PlantPort plantPort;
  private final CareProfilePort careProfilePort;

  public PlantService(PlantPort plantPort, CareProfilePort careProfilePort) {
    this.plantPort = plantPort;
    this.careProfilePort = careProfilePort;
  }

  @Override
  public Plant createPlant(Long userId, String nickname, String speciesName, String photoUrl, String location) {
    Plant newPlant = new Plant(null, userId, nickname, speciesName, photoUrl, location);
    Plant savedPlant = plantPort.save(newPlant);

    CareProfile emptyProfile = new CareProfile(
        null,
        savedPlant.getId(),
        null, null, null, null, null, null, null
    );
    careProfilePort.save(emptyProfile);

    return savedPlant;
  }

  @Override
  public Plant getPlantById(Long userId, Long plantId) {
    Plant plant = plantPort.findById(plantId)
        .orElseThrow(() -> new RuntimeException("Pflanze nicht gefunden: " + plantId));

    assertOwnership(plant, userId);
    return plant;
  }

  @Override
  public List<Plant> getPlantsForUser(Long userId) {
    return plantPort.findAllByUserId(userId);
  }

  @Override
  public void deletePlant(Long userId, Long plantId) {
    Plant plant = plantPort.findById(plantId)
        .orElseThrow(() -> new RuntimeException("Pflanze nicht gefunden: " + plantId));

    assertOwnership(plant, userId);
    careProfilePort.deleteByPlantId(plantId);
    plantPort.deleteById(plantId);
  }

  private void assertOwnership(Plant plant, Long userId) {
    if (!plant.getUserId().equals(userId)) {
      throw new RuntimeException("Keine Berechtigung für diese Pflanze");
    }
  }
}