package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.CareProfile;
import com.bagin.plantcare.ports.in.CareProfileUseCase;
import com.bagin.plantcare.rest.dto.UpdateCareProfileRequest;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CareProfileController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtAuthEntryPoint.class, JwtService.class})
class CareProfileControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean
  private CareProfileUseCase careProfileUseCase;

  private static final Long USER_ID = 1L;
  private static final Long PLANT_ID = 5L;

  private UsernamePasswordAuthenticationToken authenticatedAs(Long userId) {
    return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
  }

  @Test
  void get_authenticated_returnsCareProfile() throws Exception {
    CareProfile profile = new CareProfile(2L, PLANT_ID, "hell", 15, 25, "mittel", 7, 30, "Notizen");
    when(careProfileUseCase.getByPlantId(USER_ID, PLANT_ID)).thenReturn(profile);

    mockMvc.perform(get("/api/plants/{plantId}/care-profile", PLANT_ID)
            .with(authentication(authenticatedAs(USER_ID))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.plantId").value(PLANT_ID))
        .andExpect(jsonPath("$.lightRequirement").value("hell"));
  }

  @Test
  void get_unauthenticated_returns401() throws Exception {
    mockMvc.perform(get("/api/plants/{plantId}/care-profile", PLANT_ID))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void update_authenticated_returnsUpdatedCareProfile() throws Exception {
    CareProfile updated = new CareProfile(2L, PLANT_ID, "dunkel", 10, 20, "niedrig", 3, 14, "neu");
    when(careProfileUseCase.updateCareProfile(
        eq(USER_ID), eq(PLANT_ID), eq("dunkel"), eq(10), eq(20), eq("niedrig"), eq(3), eq(14), eq("neu")))
        .thenReturn(updated);

    UpdateCareProfileRequest request = new UpdateCareProfileRequest("dunkel", 10, 20, "niedrig", 3, 14, "neu");

    mockMvc.perform(put("/api/plants/{plantId}/care-profile", PLANT_ID)
            .with(authentication(authenticatedAs(USER_ID)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lightRequirement").value("dunkel"))
        .andExpect(jsonPath("$.notes").value("neu"));
  }
}
