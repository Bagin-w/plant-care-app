package com.bagin.plantcare.ports.in;

import com.bagin.plantcare.domain.model.PlantSpecies;

import java.util.List;

public interface PlantSpeciesUseCase {

  PlantSpecies getById(Long id);

  List<PlantSpecies> search(String query);

  List<PlantSpecies> getAll();
}