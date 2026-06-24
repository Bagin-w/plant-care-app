package com.bagin.plantcare.persistence.adapter;

import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.persistence.mapper.PlantMapper;
import com.bagin.plantcare.persistence.repository.PlantJpaRepository;
import com.bagin.plantcare.ports.out.PlantPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PlantDbAdapter implements PlantPort {

  private final PlantJpaRepository jpaRepository;
  private final PlantMapper mapper;

  public PlantDbAdapter(PlantJpaRepository jpaRepository, PlantMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Plant save(Plant plant) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(plant)));
  }

  @Override
  public Optional<Plant> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Plant> findAllByUserId(Long userId) {
    return jpaRepository.findAllByUserId(userId).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}