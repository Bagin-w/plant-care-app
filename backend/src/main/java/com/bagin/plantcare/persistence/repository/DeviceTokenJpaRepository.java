package com.bagin.plantcare.persistence.repository;

import com.bagin.plantcare.persistence.tablemodel.DeviceTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenJpaRepository extends JpaRepository<DeviceTokenEntity, Long> {

  Optional<DeviceTokenEntity> findByEndpoint(String endpoint);

  List<DeviceTokenEntity> findAllByUserId(Long userId);

  void deleteByEndpoint(String endpoint);
}