package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.ports.in.DeviceTokenUseCase;
import com.bagin.plantcare.rest.dto.RegisterDeviceRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
public class DeviceTokenController {

  private final DeviceTokenUseCase deviceTokenUseCase;

  public DeviceTokenController(DeviceTokenUseCase deviceTokenUseCase) {
    this.deviceTokenUseCase = deviceTokenUseCase;
  }

  @PostMapping
  public void register(
      @AuthenticationPrincipal Long userId,
      @RequestBody RegisterDeviceRequest request
  ) {
    deviceTokenUseCase.registerDevice(userId, request.endpoint(), request.p256dh(), request.auth());
  }
}