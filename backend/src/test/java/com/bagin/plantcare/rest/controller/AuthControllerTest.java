package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.ports.in.AuthUseCase;
import com.bagin.plantcare.rest.dto.LoginRequest;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtAuthEntryPoint.class})
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean
  private AuthUseCase authUseCase;

  @MockitoBean
  private JwtService jwtService;

  @Test
  void login_validCredentials_returnsToken() throws Exception {
    User user = new User(1L, "test@test.de", "hashedPw", "Test User");
    when(authUseCase.login("test@test.de", "rawPw")).thenReturn(user);
    when(jwtService.generateToken(1L, "test@test.de")).thenReturn("jwt-token-value");

    LoginRequest request = new LoginRequest("test@test.de", "rawPw");

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token-value"));
  }

  @Test
  void login_invalidCredentials_propagatesUnhandled() {
    when(authUseCase.login("unknown@test.de", "anyPw"))
        .thenThrow(new RuntimeException("Ungültige Anmeldedaten"));

    LoginRequest request = new LoginRequest("unknown@test.de", "anyPw");

    assertThatThrownBy(() -> mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))))
        .hasRootCauseInstanceOf(RuntimeException.class)
        .hasRootCauseMessage("Ungültige Anmeldedaten");
  }

  @Test
  void login_isPublic_doesNotRequireAuthentication() throws Exception {
    User user = new User(1L, "test@test.de", "hashedPw", "Test User");
    when(authUseCase.login("test@test.de", "rawPw")).thenReturn(user);
    when(jwtService.generateToken(1L, "test@test.de")).thenReturn("jwt-token-value");

    LoginRequest request = new LoginRequest("test@test.de", "rawPw");

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }
}
