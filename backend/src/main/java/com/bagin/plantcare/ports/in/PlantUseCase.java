package com.bagin.plantcare.ports.in;

import com.bagin.plantcare.domain.model.Plant;

import java.util.List;

public interface PlantUseCase {

  Plant createPlant(Long userId, String nickname, String speciesName, String photoUrl, String location);

  Plant getPlantById(Long userId, Long plantId);

  List<Plant> getPlantsForUser(Long userId);

  Plant updatePlant(Long userId, Long plantId, String nickname, String speciesName, String photoUrl, String location);

  void deletePlant(Long userId, Long plantId);
}