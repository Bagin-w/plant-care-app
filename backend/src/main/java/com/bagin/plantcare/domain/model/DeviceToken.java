package com.bagin.plantcare.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DeviceToken {

  private final Long id;
  private final Long userId;
  private final String endpoint;
  private final String p256dh;
  private final String auth;
}