package com.bagin.plantcare.ports.in;

import com.bagin.plantcare.domain.model.DeviceToken;

public interface DeviceTokenUseCase {

  DeviceToken registerDevice(Long userId, String endpoint, String p256dh, String auth);
}