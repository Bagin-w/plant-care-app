package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.CareProfile;
import com.bagin.plantcare.ports.in.CareProfileUseCase;
import com.bagin.plantcare.rest.dto.CareProfileResponse;
import com.bagin.plantcare.rest.dto.UpdateCareProfileRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plants/{plantId}/care-profile")
public class CareProfileController {

  private final CareProfileUseCase careProfileUseCase;

  public CareProfileController(CareProfileUseCase careProfileUseCase) {
    this.careProfileUseCase = careProfileUseCase;
  }

  @GetMapping
  public CareProfileResponse get(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long plantId
  ) {
    return CareProfileResponse.fromDomain(careProfileUseCase.getByPlantId(userId, plantId));
  }

  @PutMapping
  public CareProfileResponse update(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long plantId,
      @RequestBody UpdateCareProfileRequest request
  ) {
    CareProfile updated = careProfileUseCase.updateCareProfile(
        userId,
        plantId,
        request.lightRequirement(),
        request.temperatureMin(),
        request.temperatureMax(),
        request.humidityRequirement(),
        request.wateringIntervalDays(),
        request.fertilizingIntervalDays(),
        request.notes()
    );
    return CareProfileResponse.fromDomain(updated);
  }
}