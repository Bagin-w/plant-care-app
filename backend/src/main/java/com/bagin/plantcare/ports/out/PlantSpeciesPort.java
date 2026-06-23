package com.bagin.plantcare.ports.out;

import com.bagin.plantcare.domain.model.PlantSpecies;

import java.util.List;
import java.util.Optional;

public interface PlantSpeciesPort {

  PlantSpecies save(PlantSpecies species);

  Optional<PlantSpecies> findById(Long id);

  Optional<PlantSpecies> findByPerenualId(Long perenualId);

  List<PlantSpecies> findAll();

  List<PlantSpecies> searchByName(String query);
}