package com.bagin.plantcare.persistence.adapter;

import com.bagin.plantcare.domain.model.PlantSpecies;
import com.bagin.plantcare.persistence.mapper.PlantSpeciesMapper;
import com.bagin.plantcare.persistence.repository.PlantSpeciesJpaRepository;
import com.bagin.plantcare.ports.out.PlantSpeciesPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PlantSpeciesDbAdapter implements PlantSpeciesPort {

  private final PlantSpeciesJpaRepository jpaRepository;
  private final PlantSpeciesMapper mapper;

  public PlantSpeciesDbAdapter(PlantSpeciesJpaRepository jpaRepository, PlantSpeciesMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public PlantSpecies save(PlantSpecies species) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(species)));
  }

  @Override
  public Optional<PlantSpecies> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<PlantSpecies> findByPerenualId(Long perenualId) {
    return jpaRepository.findByPerenualId(perenualId).map(mapper::toDomain);
  }

  @Override
  public List<PlantSpecies> findAll() {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<PlantSpecies> searchByName(String query) {
    return jpaRepository.findByCommonNameContainingIgnoreCase(query).stream()
        .map(mapper::toDomain)
        .toList();
  }
}