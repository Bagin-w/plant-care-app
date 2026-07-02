package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.ReminderRule;
import com.bagin.plantcare.ports.in.ReminderRuleUseCase;
import com.bagin.plantcare.rest.security.JwtAuthEntryPoint;
import com.bagin.plantcare.rest.security.JwtAuthFilter;
import com.bagin.plantcare.rest.security.JwtService;
import com.bagin.plantcare.rest.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReminderRuleController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtAuthEntryPoint.class, JwtService.class})
class ReminderRuleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ReminderRuleUseCase reminderRuleUseCase;

  private static final Long USER_ID = 1L;
  private static final Long PLANT_ID = 5L;

  private UsernamePasswordAuthenticationToken authenticatedAs(Long userId) {
    return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
  }

  // Request bodies with a LocalTime field are written as raw JSON here rather than via
  // ObjectMapper.writeValueAsString, since jackson-datatype-jsr310 is not on the test
  // classpath as a direct/compile dependency (only pulled in for the app's own runtime
  // HttpMessageConverters, which is what actually serializes the JSON responses below).
  private static final String CREATE_REMINDER_JSON =
      "{\"type\":\"WATERING\",\"customLabel\":null,\"intervalDays\":3,\"preferredTime\":\"09:00:00\"}";

  @Test
  void create_authenticated_returnsCreatedReminder() throws Exception {
    LocalTime preferredTime = LocalTime.of(9, 0);
    ReminderRule created = new ReminderRule(
        2L, PLANT_ID, ReminderRule.Type.WATERING, null, 3, preferredTime, null, LocalDateTime.now(), true);
    when(reminderRuleUseCase.createReminder(
        eq(USER_ID), eq(PLANT_ID), eq(ReminderRule.Type.WATERING), eq((String) null), eq(3), eq(preferredTime)))
        .thenReturn(created);

    mockMvc.perform(post("/api/plants/{plantId}/reminders", PLANT_ID)
            .with(authentication(authenticatedAs(USER_ID)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(CREATE_REMINDER_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.plantId").value(PLANT_ID))
        .andExpect(jsonPath("$.type").value("WATERING"))
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  void create_unauthenticated_returns401() throws Exception {
    mockMvc.perform(post("/api/plants/{plantId}/reminders", PLANT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(CREATE_REMINDER_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getAll_authenticated_returnsRemindersForPlant() throws Exception {
    List<ReminderRule> rules = List.of(
        new ReminderRule(2L, PLANT_ID, ReminderRule.Type.WATERING, null, 3, LocalTime.NOON, null, LocalDateTime.now(), true));
    when(reminderRuleUseCase.getRemindersForPlant(USER_ID, PLANT_ID)).thenReturn(rules);

    mockMvc.perform(get("/api/plants/{plantId}/reminders", PLANT_ID)
            .with(authentication(authenticatedAs(USER_ID))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(2))
        .andExpect(jsonPath("$[0].type").value("WATERING"));
  }

  @Test
  void deactivate_authenticated_delegatesToUseCase() throws Exception {
    mockMvc.perform(patch("/api/reminders/{reminderId}/deactivate", 2L)
            .with(authentication(authenticatedAs(USER_ID))))
        .andExpect(status().isOk());

    verify(reminderRuleUseCase).deactivateReminder(USER_ID, 2L);
  }

  @Test
  void activate_authenticated_delegatesToUseCase() throws Exception {
    mockMvc.perform(patch("/api/reminders/{reminderId}/activate", 2L)
            .with(authentication(authenticatedAs(USER_ID))))
        .andExpect(status().isOk());

    verify(reminderRuleUseCase).activateReminder(USER_ID, 2L);
  }

  @Test
  void delete_authenticated_delegatesToUseCase() throws Exception {
    mockMvc.perform(delete("/api/reminders/{reminderId}", 2L)
            .with(authentication(authenticatedAs(USER_ID))))
        .andExpect(status().isOk());

    verify(reminderRuleUseCase).deleteReminder(USER_ID, 2L);
  }
}
