package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.ports.in.PlantUseCase;
import com.bagin.plantcare.rest.dto.CreatePlantRequest;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlantController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtAuthEntryPoint.class, JwtService.class})
class PlantControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean
  private PlantUseCase plantUseCase;

  private static final Long USER_ID = 1L;

  private UsernamePasswordAuthenticationToken authenticatedAs(Long userId) {
    return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
  }

  @Test
  void createPlant_authenticated_returnsCreatedPlant() throws Exception {
    Plant created = new Plant(10L, USER_ID, "Ficus", "Ficus lyrata", "photo.jpg", "Wohnzimmer");
    when(plantUseCase.createPlant(eq(USER_ID), eq("Ficus"), eq("Ficus lyrata"), eq("photo.jpg"), eq("Wohnzimmer")))
        .thenReturn(created);

    CreatePlantRequest request = new CreatePlantRequest("Ficus", "Ficus lyrata", "photo.jpg", "Wohnzimmer");

    mockMvc.perform(post("/api/plants")
            .with(authentication(authenticatedAs(USER_ID)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.nickname").value("Ficus"));
  }

  @Test
  void createPlant_unauthenticated_returns401() throws Exception {
    CreatePlantRequest request = new CreatePlantRequest("Ficus", null, null, null);

    mockMvc.perform(post("/api/plants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getAll_authenticated_returnsPlantsForUser() throws Exception {
    List<Plant> plants = List.of(new Plant(1L, USER_ID, "Ficus", null, null, null));
    when(plantUseCase.getPlantsForUser(USER_ID)).thenReturn(plants);

    mockMvc.perform(get("/api/plants").with(authentication(authenticatedAs(USER_ID))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].nickname").value("Ficus"));
  }

  @Test
  void getById_serviceThrows_propagatesUnhandled() {
    // No @ControllerAdvice/@ExceptionHandler exists in this app, so a service-layer
    // RuntimeException is not translated into a clean HTTP response within MockMvc's
    // dispatch - it propagates up through perform() wrapped in a ServletException.
    when(plantUseCase.getPlantById(USER_ID, 99L))
        .thenThrow(new RuntimeException("Pflanze nicht gefunden: 99"));

    assertThatThrownBy(() -> mockMvc.perform(get("/api/plants/99").with(authentication(authenticatedAs(USER_ID)))))
        .hasRootCauseInstanceOf(RuntimeException.class)
        .hasRootCauseMessage("Pflanze nicht gefunden: 99");
  }

  @Test
  void deletePlant_authenticated_delegatesToUseCase() throws Exception {
    mockMvc.perform(delete("/api/plants/5").with(authentication(authenticatedAs(USER_ID))))
        .andExpect(status().isOk());

    verify(plantUseCase).deletePlant(USER_ID, 5L);
  }
}
