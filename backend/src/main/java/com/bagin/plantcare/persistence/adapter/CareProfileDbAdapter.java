package com.bagin.plantcare.persistence.adapter;

import com.bagin.plantcare.domain.model.CareProfile;
import com.bagin.plantcare.persistence.mapper.CareProfileMapper;
import com.bagin.plantcare.persistence.repository.CareProfileJpaRepository;
import com.bagin.plantcare.ports.out.CareProfilePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CareProfileDbAdapter implements CareProfilePort {

  private final CareProfileJpaRepository jpaRepository;
  private final CareProfileMapper mapper;

  public CareProfileDbAdapter(CareProfileJpaRepository jpaRepository, CareProfileMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public CareProfile save(CareProfile careProfile) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(careProfile)));
  }

  @Override
  public Optional<CareProfile> findByPlantId(Long plantId) {
    return jpaRepository.findByPlantId(plantId).map(mapper::toDomain);
  }

  @Override
  public void deleteByPlantId(Long plantId) {
    jpaRepository.deleteByPlantId(plantId);
  }
}