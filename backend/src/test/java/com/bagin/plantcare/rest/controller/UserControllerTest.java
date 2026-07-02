package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.ports.in.UserUseCase;
import com.bagin.plantcare.rest.dto.RegisterUserRequest;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtAuthEntryPoint.class, JwtService.class})
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean
  private UserUseCase userUseCase;

  private UsernamePasswordAuthenticationToken authenticatedAs(Long userId) {
    return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
  }

  @Test
  void register_isPublic_returnsCreatedUser() throws Exception {
    User created = new User(1L, "test@test.de", "hashedPw", "Test User");
    when(userUseCase.registerUser("test@test.de", "rawPw", "Test User")).thenReturn(created);

    RegisterUserRequest request = new RegisterUserRequest("test@test.de", "rawPw", "Test User");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email").value("test@test.de"))
        .andExpect(jsonPath("$.name").value("Test User"));
  }

  @Test
  void getById_authenticated_returnsUser() throws Exception {
    User user = new User(1L, "test@test.de", "hashedPw", "Test User");
    when(userUseCase.getUserById(1L)).thenReturn(user);

    mockMvc.perform(get("/api/users/1").with(authentication(authenticatedAs(1L))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email").value("test@test.de"));
  }

  @Test
  void getById_unauthenticated_returns401() throws Exception {
    mockMvc.perform(get("/api/users/1"))
        .andExpect(status().isUnauthorized());
  }
}
