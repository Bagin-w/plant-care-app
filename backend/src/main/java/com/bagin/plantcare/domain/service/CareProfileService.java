package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.CareProfile;
import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.ports.in.CareProfileUseCase;
import com.bagin.plantcare.ports.out.CareProfilePort;
import com.bagin.plantcare.ports.out.PlantPort;
import org.springframework.stereotype.Service;

@Service
public class CareProfileService implements CareProfileUseCase {

  private final CareProfilePort careProfilePort;
  private final PlantPort plantPort;

  public CareProfileService(CareProfilePort careProfilePort, PlantPort plantPort) {
    this.careProfilePort = careProfilePort;
    this.plantPort = plantPort;
  }

  @Override
  public CareProfile getByPlantId(Long userId, Long plantId) {
    assertPlantOwnership(userId, plantId);

    return careProfilePort.findByPlantId(plantId)
        .orElseThrow(() -> new RuntimeException("Pflegeprofil nicht gefunden für Pflanze: " + plantId));
  }

  @Override
  public CareProfile updateCareProfile(
      Long userId,
      Long plantId,
      String lightRequirement,
      Integer temperatureMin,
      Integer temperatureMax,
      String humidityRequirement,
      Integer wateringIntervalDays,
      Integer fertilizingIntervalDays,
      String notes
  ) {
    assertPlantOwnership(userId, plantId);

    CareProfile existing = careProfilePort.findByPlantId(plantId)
        .orElseThrow(() -> new RuntimeException("Pflegeprofil nicht gefunden für Pflanze: " + plantId));

    CareProfile updated = new CareProfile(
        existing.getId(),
        plantId,
        lightRequirement,
        temperatureMin,
        temperatureMax,
        humidityRequirement,
        wateringIntervalDays,
        fertilizingIntervalDays,
        notes
    );

    return careProfilePort.save(updated);
  }

  private void assertPlantOwnership(Long userId, Long plantId) {
    Plant plant = plantPort.findById(plantId)
        .orElseThrow(() -> new RuntimeException("Pflanze nicht gefunden: " + plantId));

    if (!plant.getUserId().equals(userId)) {
      throw new RuntimeException("Keine Berechtigung für diese Pflanze");
    }
  }
}