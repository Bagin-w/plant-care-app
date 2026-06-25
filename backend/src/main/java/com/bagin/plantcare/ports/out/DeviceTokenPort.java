package com.bagin.plantcare.ports.out;

import com.bagin.plantcare.domain.model.DeviceToken;

import java.util.List;

public interface DeviceTokenPort {

  DeviceToken save(DeviceToken deviceToken);

  List<DeviceToken> findAllByUserId(Long userId);

  void deleteByEndpoint(String endpoint);
}