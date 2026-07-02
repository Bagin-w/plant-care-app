package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.ports.in.DeviceTokenUseCase;
import com.bagin.plantcare.rest.dto.RegisterDeviceRequest;
import com.bagin.plantcare.rest.security.JwtAuthEntryPoint;
import com.bagin.plantcare.rest.security.JwtAuthFilter;
import com.bagin.plantcare.rest.security.JwtService;
import com.bagin.plantcare.rest.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceTokenController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtAuthEntryPoint.class, JwtService.class})
class DeviceTokenControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean
  private DeviceTokenUseCase deviceTokenUseCase;

  private static final Long USER_ID = 1L;

  private UsernamePasswordAuthenticationToken authenticatedAs(Long userId) {
    return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
  }

  @Test
  void register_authenticated_delegatesToUseCase() throws Exception {
    RegisterDeviceRequest request = new RegisterDeviceRequest(
        "https://push.example.com/sub/abc", "p256dhKey", "authKey");

    mockMvc.perform(post("/api/devices")
            .with(authentication(authenticatedAs(USER_ID)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(deviceTokenUseCase).registerDevice(
        USER_ID, "https://push.example.com/sub/abc", "p256dhKey", "authKey");
  }

  @Test
  void register_unauthenticated_returns401() throws Exception {
    RegisterDeviceRequest request = new RegisterDeviceRequest("endpoint", "p256dh", "auth");

    mockMvc.perform(post("/api/devices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
