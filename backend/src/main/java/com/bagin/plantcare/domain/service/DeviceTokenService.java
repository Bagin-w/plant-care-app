package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.DeviceToken;
import com.bagin.plantcare.ports.in.DeviceTokenUseCase;
import com.bagin.plantcare.ports.out.DeviceTokenPort;
import org.springframework.stereotype.Service;

@Service
public class DeviceTokenService implements DeviceTokenUseCase {

  private final DeviceTokenPort deviceTokenPort;

  public DeviceTokenService(DeviceTokenPort deviceTokenPort) {
    this.deviceTokenPort = deviceTokenPort;
  }

  @Override
  public DeviceToken registerDevice(Long userId, String endpoint, String p256dh, String auth) {
    DeviceToken token = new DeviceToken(null, userId, endpoint, p256dh, auth);
    return deviceTokenPort.save(token);
  }
}