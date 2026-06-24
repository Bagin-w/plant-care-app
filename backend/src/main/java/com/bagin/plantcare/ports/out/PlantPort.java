package com.bagin.plantcare.ports.out;

import com.bagin.plantcare.domain.model.Plant;

import java.util.List;
import java.util.Optional;

public interface PlantPort {

  Plant save(Plant plant);

  Optional<Plant> findById(Long id);

  List<Plant> findAllByUserId(Long userId);

  void deleteById(Long id);
}