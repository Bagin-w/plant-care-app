package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.DeviceToken;
import com.bagin.plantcare.ports.out.DeviceTokenPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Dedup key is the push-subscription endpoint alone (see DeviceTokenService.registerDevice).
 * If a token already exists for an endpoint it is returned unchanged, even if the caller
 * supplied a different userId/p256dh/auth for that same endpoint.
 */
@ExtendWith(MockitoExtension.class)
class DeviceTokenServiceTest {

  @Mock
  private DeviceTokenPort deviceTokenPort;

  private DeviceTokenService deviceTokenService;

  private static final String ENDPOINT = "https://push.example.com/sub/abc";

  @BeforeEach
  void setUp() {
    deviceTokenService = new DeviceTokenService(deviceTokenPort);
  }

  @Test
  void noExistingToken_createsAndSavesNewToken() {
    when(deviceTokenPort.findByEndpoint(ENDPOINT)).thenReturn(Optional.empty());
    when(deviceTokenPort.save(any(DeviceToken.class))).thenAnswer(inv -> inv.getArgument(0));

    DeviceToken result = deviceTokenService.registerDevice(1L, ENDPOINT, "p256dhKey", "authKey");

    ArgumentCaptor<DeviceToken> captor = ArgumentCaptor.forClass(DeviceToken.class);
    verify(deviceTokenPort).save(captor.capture());
    DeviceToken saved = captor.getValue();

    assertThat(saved.getId()).isNull();
    assertThat(saved.getUserId()).isEqualTo(1L);
    assertThat(saved.getEndpoint()).isEqualTo(ENDPOINT);
    assertThat(saved.getP256dh()).isEqualTo("p256dhKey");
    assertThat(saved.getAuth()).isEqualTo("authKey");
    assertThat(result).isEqualTo(saved);
  }

  @Test
  void existingTokenForEndpoint_returnsExistingWithoutSaving() {
    DeviceToken existing = new DeviceToken(9L, 1L, ENDPOINT, "oldP256dh", "oldAuth");
    when(deviceTokenPort.findByEndpoint(ENDPOINT)).thenReturn(Optional.of(existing));

    DeviceToken result = deviceTokenService.registerDevice(1L, ENDPOINT, "oldP256dh", "oldAuth");

    assertThat(result).isEqualTo(existing);
    verify(deviceTokenPort, never()).save(any());
  }

  @Test
  void existingTokenForEndpoint_ignoresDifferentUserIdAndKeysFromCaller() {
    DeviceToken existing = new DeviceToken(9L, 1L, ENDPOINT, "oldP256dh", "oldAuth");
    when(deviceTokenPort.findByEndpoint(ENDPOINT)).thenReturn(Optional.of(existing));

    DeviceToken result = deviceTokenService.registerDevice(2L, ENDPOINT, "newP256dh", "newAuth");

    assertThat(result).isEqualTo(existing);
    assertThat(result.getUserId()).isEqualTo(1L);
    assertThat(result.getP256dh()).isEqualTo("oldP256dh");
    assertThat(result.getAuth()).isEqualTo("oldAuth");
    verify(deviceTokenPort, never()).save(any());
  }
}
