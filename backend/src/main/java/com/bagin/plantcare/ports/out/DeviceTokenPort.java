package com.bagin.plantcare.ports.out;

import com.bagin.plantcare.domain.model.DeviceToken;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenPort {

  DeviceToken save(DeviceToken deviceToken);

  Optional<DeviceToken> findByEndpoint(String endpoint);

  List<DeviceToken> findAllByUserId(Long userId);

  void deleteByEndpoint(String endpoint);
}