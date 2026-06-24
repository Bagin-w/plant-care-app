package com.bagin.plantcare.persistence.repository;

import com.bagin.plantcare.persistence.tablemodel.CareProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CareProfileJpaRepository extends JpaRepository<CareProfileEntity, Long> {

  Optional<CareProfileEntity> findByPlantId(Long plantId);

  void deleteByPlantId(Long plantId);
}