package com.bagin.plantcare.persistence.repository;

import com.bagin.plantcare.persistence.tablemodel.PlantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantJpaRepository extends JpaRepository<PlantEntity, Long> {

  List<PlantEntity> findAllByUserId(Long userId);
}