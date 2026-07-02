package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.domain.model.ReminderRule;
import com.bagin.plantcare.ports.out.PlantPort;
import com.bagin.plantcare.ports.out.ReminderRulePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * ReminderRuleService computes nextDueAt via LocalDate.now()/LocalDateTime.now() directly
 * (no injected Clock). These tests therefore build the expected value using the same
 * "now" semantics rather than mocking time. Tests that place preferredTime within the
 * current day carry a small, accepted flakiness risk right around midnight.
 */
@ExtendWith(MockitoExtension.class)
class ReminderRuleServiceTest {

  @Mock
  private ReminderRulePort reminderRulePort;

  @Mock
  private PlantPort plantPort;

  private ReminderRuleService reminderRuleService;

  private static final Long USER_ID = 1L;
  private static final Long PLANT_ID = 5L;

  @BeforeEach
  void setUp() {
    reminderRuleService = new ReminderRuleService(reminderRulePort, plantPort);
  }

  private void ownedPlantExists() {
    Plant plant = new Plant(PLANT_ID, USER_ID, "Ficus", null, null, null);
    when(plantPort.findById(PLANT_ID)).thenReturn(Optional.of(plant));
  }

  @Nested
  class CreateReminder {

    @Test
    void preferredTimeStillAheadToday_nextDueAtIsTodayAtThatTime() {
      ownedPlantExists();
      when(reminderRulePort.save(any(ReminderRule.class))).thenAnswer(inv -> inv.getArgument(0));

      LocalTime preferredTime = LocalTime.now().plusHours(2).withNano(0);
      LocalDateTime expected = LocalDateTime.of(LocalDate.now(), preferredTime);

      ReminderRule result = reminderRuleService.createReminder(
          USER_ID, PLANT_ID, ReminderRule.Type.WATERING, null, 3, preferredTime);

      assertThat(result.getNextDueAt()).isEqualTo(expected);
      assertThat(result.getPlantId()).isEqualTo(PLANT_ID);
      assertThat(result.isActive()).isTrue();
    }

    @Test
    void preferredTimeAlreadyPassedToday_nextDueAtIsTodayPlusInterval() {
      ownedPlantExists();
      when(reminderRulePort.save(any(ReminderRule.class))).thenAnswer(inv -> inv.getArgument(0));

      LocalTime preferredTime = LocalTime.now().minusHours(1).withNano(0);
      int intervalDays = 3;
      LocalDateTime expected = LocalDateTime.of(LocalDate.now(), preferredTime).plusDays(intervalDays);

      ReminderRule result = reminderRuleService.createReminder(
          USER_ID, PLANT_ID, ReminderRule.Type.WATERING, null, intervalDays, preferredTime);

      assertThat(result.getNextDueAt()).isEqualTo(expected);
    }

    @Test
    void customType_keepsCustomLabel() {
      ownedPlantExists();
      when(reminderRulePort.save(any(ReminderRule.class))).thenAnswer(inv -> inv.getArgument(0));

      ReminderRule result = reminderRuleService.createReminder(
          USER_ID, PLANT_ID, ReminderRule.Type.CUSTOM, "Umtopfen", 7, LocalTime.NOON);

      assertThat(result.getCustomLabel()).isEqualTo("Umtopfen");
    }

    @Test
    void nonCustomType_dropsCustomLabel() {
      ownedPlantExists();
      when(reminderRulePort.save(any(ReminderRule.class))).thenAnswer(inv -> inv.getArgument(0));

      ReminderRule result = reminderRuleService.createReminder(
          USER_ID, PLANT_ID, ReminderRule.Type.WATERING, "ignored", 7, LocalTime.NOON);

      assertThat(result.getCustomLabel()).isNull();
    }

    @Test
    void plantMissing_throwsAndSkipsPersistence() {
      when(plantPort.findById(PLANT_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> reminderRuleService.createReminder(
          USER_ID, PLANT_ID, ReminderRule.Type.WATERING, null, 3, LocalTime.NOON))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Pflanze nicht gefunden: " + PLANT_ID);

      verifyNoInteractions(reminderRulePort);
    }

    @Test
    void differentOwner_throwsAndSkipsPersistence() {
      Plant plant = new Plant(PLANT_ID, USER_ID, "Ficus", null, null, null);
      when(plantPort.findById(PLANT_ID)).thenReturn(Optional.of(plant));

      assertThatThrownBy(() -> reminderRuleService.createReminder(
          2L, PLANT_ID, ReminderRule.Type.WATERING, null, 3, LocalTime.NOON))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Keine Berechtigung für diese Pflanze");

      verifyNoInteractions(reminderRulePort);
    }

    @Test
    void nullIntervalDays_throwsAndSkipsPersistence() {
      ownedPlantExists();

      assertThatThrownBy(() -> reminderRuleService.createReminder(
          USER_ID, PLANT_ID, ReminderRule.Type.WATERING, null, null, LocalTime.NOON))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Intervall muss mindestens 1 Tag betragen");

      verify(reminderRulePort, never()).save(any());
    }

    @Test
    void intervalDaysBelowOne_throwsAndSkipsPersistence() {
      ownedPlantExists();

      assertThatThrownBy(() -> reminderRuleService.createReminder(
          USER_ID, PLANT_ID, ReminderRule.Type.WATERING, null, 0, LocalTime.NOON))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Intervall muss mindestens 1 Tag betragen");

      verify(reminderRulePort, never()).save(any());
    }

    @Test
    void missingPreferredTime_throwsAndSkipsPersistence() {
      ownedPlantExists();

      assertThatThrownBy(() -> reminderRuleService.createReminder(
          USER_ID, PLANT_ID, ReminderRule.Type.WATERING, null, 3, null))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Eine Uhrzeit für die Erinnerung ist erforderlich");

      verify(reminderRulePort, never()).save(any());
    }
  }

  @Nested
  class GetRemindersForPlant {

    @Test
    void ownerMatches_returnsRemindersFromPort() {
      ownedPlantExists();
      List<ReminderRule> rules = List.of(
          new ReminderRule(1L, PLANT_ID, ReminderRule.Type.WATERING, null, 3, LocalTime.NOON, null, LocalDateTime.now(), true));
      when(reminderRulePort.findAllByPlantId(PLANT_ID)).thenReturn(rules);

      List<ReminderRule> result = reminderRuleService.getRemindersForPlant(USER_ID, PLANT_ID);

      assertThat(result).isEqualTo(rules);
    }

    @Test
    void differentOwner_throwsUnauthorized() {
      Plant plant = new Plant(PLANT_ID, USER_ID, "Ficus", null, null, null);
      when(plantPort.findById(PLANT_ID)).thenReturn(Optional.of(plant));

      assertThatThrownBy(() -> reminderRuleService.getRemindersForPlant(2L, PLANT_ID))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Keine Berechtigung für diese Pflanze");
    }
  }

  @Nested
  class DeleteReminder {

    @Test
    void ownerMatches_deletesReminder() {
      ReminderRule rule = new ReminderRule(2L, PLANT_ID, ReminderRule.Type.WATERING, null, 3, LocalTime.NOON, null, LocalDateTime.now(), true);
      when(reminderRulePort.findById(2L)).thenReturn(Optional.of(rule));
      ownedPlantExists();

      reminderRuleService.deleteReminder(USER_ID, 2L);

      verify(reminderRulePort).deleteById(2L);
    }

    @Test
    void reminderMissing_throwsNotFound() {
      when(reminderRulePort.findById(2L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> reminderRuleService.deleteReminder(USER_ID, 2L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Erinnerung nicht gefunden: 2");

      verify(reminderRulePort, never()).deleteById(any());
    }

    @Test
    void differentOwner_throwsAndSkipsDeletion() {
      ReminderRule rule = new ReminderRule(2L, PLANT_ID, ReminderRule.Type.WATERING, null, 3, LocalTime.NOON, null, LocalDateTime.now(), true);
      when(reminderRulePort.findById(2L)).thenReturn(Optional.of(rule));
      Plant plant = new Plant(PLANT_ID, USER_ID, "Ficus", null, null, null);
      when(plantPort.findById(PLANT_ID)).thenReturn(Optional.of(plant));

      assertThatThrownBy(() -> reminderRuleService.deleteReminder(2L, 2L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Keine Berechtigung für diese Pflanze");

      verify(reminderRulePort, never()).deleteById(any());
    }
  }

  @Nested
  class ToggleActiveState {

    @Test
    void deactivateReminder_setsActiveFalsePreservingOtherFields() {
      LocalDateTime nextDueAt = LocalDateTime.now().plusDays(1);
      ReminderRule rule = new ReminderRule(2L, PLANT_ID, ReminderRule.Type.WATERING, null, 3, LocalTime.NOON, null, nextDueAt, true);
      when(reminderRulePort.findById(2L)).thenReturn(Optional.of(rule));
      ownedPlantExists();
      when(reminderRulePort.save(any(ReminderRule.class))).thenAnswer(inv -> inv.getArgument(0));

      reminderRuleService.deactivateReminder(USER_ID, 2L);

      ArgumentCaptor<ReminderRule> captor = ArgumentCaptor.forClass(ReminderRule.class);
      verify(reminderRulePort).save(captor.capture());
      ReminderRule saved = captor.getValue();
      assertThat(saved.isActive()).isFalse();
      assertThat(saved.getId()).isEqualTo(2L);
      assertThat(saved.getPlantId()).isEqualTo(PLANT_ID);
      assertThat(saved.getIntervalDays()).isEqualTo(3);
      assertThat(saved.getPreferredTime()).isEqualTo(LocalTime.NOON);
      assertThat(saved.getNextDueAt()).isEqualTo(nextDueAt);
    }

    @Test
    void activateReminder_setsActiveTrue() {
      ReminderRule rule = new ReminderRule(2L, PLANT_ID, ReminderRule.Type.WATERING, null, 3, LocalTime.NOON, null, LocalDateTime.now(), false);
      when(reminderRulePort.findById(2L)).thenReturn(Optional.of(rule));
      ownedPlantExists();
      when(reminderRulePort.save(any(ReminderRule.class))).thenAnswer(inv -> inv.getArgument(0));

      reminderRuleService.activateReminder(USER_ID, 2L);

      ArgumentCaptor<ReminderRule> captor = ArgumentCaptor.forClass(ReminderRule.class);
      verify(reminderRulePort).save(captor.capture());
      assertThat(captor.getValue().isActive()).isTrue();
    }
  }
}
