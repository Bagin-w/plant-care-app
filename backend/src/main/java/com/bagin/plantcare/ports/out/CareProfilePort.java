package com.bagin.plantcare.ports.out;

import com.bagin.plantcare.domain.model.CareProfile;

import java.util.Optional;

public interface CareProfilePort {

  CareProfile save(CareProfile careProfile);

  Optional<CareProfile> findByPlantId(Long plantId);

  void deleteByPlantId(Long plantId);
}