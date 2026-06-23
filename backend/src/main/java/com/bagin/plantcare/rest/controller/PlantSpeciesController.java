package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.PlantSpecies;
import com.bagin.plantcare.ports.in.PlantSpeciesUseCase;
import com.bagin.plantcare.rest.dto.PlantSpeciesResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plant-species")
public class PlantSpeciesController {

  private final PlantSpeciesUseCase plantSpeciesUseCase;

  public PlantSpeciesController(PlantSpeciesUseCase plantSpeciesUseCase) {
    this.plantSpeciesUseCase = plantSpeciesUseCase;
  }

  @GetMapping
  public List<PlantSpeciesResponse> getAll() {
    return plantSpeciesUseCase.getAll().stream()
        .map(PlantSpeciesResponse::fromDomain)
        .toList();
  }

  @GetMapping("/{id}")
  public PlantSpeciesResponse getById(@PathVariable Long id) {
    return PlantSpeciesResponse.fromDomain(plantSpeciesUseCase.getById(id));
  }

  @GetMapping("/search")
  public List<PlantSpeciesResponse> search(@RequestParam String query) {
    return plantSpeciesUseCase.search(query).stream()
        .map(PlantSpeciesResponse::fromDomain)
        .toList();
  }
}