package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.ports.in.PlantUseCase;
import com.bagin.plantcare.rest.dto.CreatePlantRequest;
import com.bagin.plantcare.rest.dto.PlantResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

  private final PlantUseCase plantUseCase;

  public PlantController(PlantUseCase plantUseCase) {
    this.plantUseCase = plantUseCase;
  }

  @PostMapping
  public PlantResponse create(
      @AuthenticationPrincipal Long userId,
      @RequestBody CreatePlantRequest request
  ) {
    Plant plant = plantUseCase.createPlant(
        userId,
        request.nickname(),
        request.speciesName(),
        request.photoUrl(),
        request.location()
    );
    return PlantResponse.fromDomain(plant);
  }

  @GetMapping
  public List<PlantResponse> getAll(@AuthenticationPrincipal Long userId) {
    return plantUseCase.getPlantsForUser(userId).stream()
        .map(PlantResponse::fromDomain)
        .toList();
  }

  @GetMapping("/{id}")
  public PlantResponse getById(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long id
  ) {
    return PlantResponse.fromDomain(plantUseCase.getPlantById(userId, id));
  }

  @DeleteMapping("/{id}")
  public void delete(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long id
  ) {
    plantUseCase.deletePlant(userId, id);
  }

  @PutMapping("/{id}")
  public PlantResponse update(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long id,
      @RequestBody CreatePlantRequest request
  ) {
    Plant updated = plantUseCase.updatePlant(
        userId, id, request.nickname(), request.speciesName(), request.photoUrl(), request.location()
    );
    return PlantResponse.fromDomain(updated);
  }
}