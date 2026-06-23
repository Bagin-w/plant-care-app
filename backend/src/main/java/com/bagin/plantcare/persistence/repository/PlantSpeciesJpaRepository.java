package com.bagin.plantcare.persistence.repository;

import com.bagin.plantcare.persistence.tablemodel.PlantSpeciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlantSpeciesJpaRepository extends JpaRepository<PlantSpeciesEntity, Long> {

  Optional<PlantSpeciesEntity> findByPerenualId(Long perenualId);

  List<PlantSpeciesEntity> findByCommonNameContainingIgnoreCase(String query);
}