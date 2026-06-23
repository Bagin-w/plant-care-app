package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.PlantSpecies;
import com.bagin.plantcare.ports.in.PlantSpeciesUseCase;
import com.bagin.plantcare.ports.out.PlantSpeciesPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantSpeciesService implements PlantSpeciesUseCase {

  private final PlantSpeciesPort plantSpeciesPort;

  public PlantSpeciesService(PlantSpeciesPort plantSpeciesPort) {
    this.plantSpeciesPort = plantSpeciesPort;
  }

  @Override
  public PlantSpecies getById(Long id) {
    return plantSpeciesPort.findById(id)
        .orElseThrow(() -> new RuntimeException("Pflanzenart nicht gefunden: " + id));
  }

  @Override
  public List<PlantSpecies> search(String query) {
    return plantSpeciesPort.searchByName(query);
  }

  @Override
  public List<PlantSpecies> getAll() {
    return plantSpeciesPort.findAll();
  }
}