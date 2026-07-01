package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.domain.model.ReminderRule;
import com.bagin.plantcare.ports.in.ReminderRuleUseCase;
import com.bagin.plantcare.ports.out.PlantPort;
import com.bagin.plantcare.ports.out.ReminderRulePort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReminderRuleService implements ReminderRuleUseCase {

  private final ReminderRulePort reminderRulePort;
  private final PlantPort plantPort;

  public ReminderRuleService(ReminderRulePort reminderRulePort, PlantPort plantPort) {
    this.reminderRulePort = reminderRulePort;
    this.plantPort = plantPort;
  }

  @Override
  public ReminderRule createReminder(
      Long userId,
      Long plantId,
      ReminderRule.Type type,
      String customLabel,
      Integer intervalDays,
      LocalTime preferredTime
  ) {
    assertPlantOwnership(userId, plantId);

    if (intervalDays == null || intervalDays < 1) {
      throw new RuntimeException("Intervall muss mindestens 1 Tag betragen");
    }

    if (preferredTime == null) {
      throw new RuntimeException("Eine Uhrzeit für die Erinnerung ist erforderlich");
    }

    LocalDateTime nextDueAt = calculateInitialDueDate(intervalDays, preferredTime);

    ReminderRule newRule = new ReminderRule(
        null,
        plantId,
        type,
        type == ReminderRule.Type.CUSTOM ? customLabel : null,
        intervalDays,
        preferredTime,
        null,
        nextDueAt,
        true
    );

    return reminderRulePort.save(newRule);
  }

  private LocalDateTime calculateInitialDueDate(Integer intervalDays, LocalTime preferredTime) {
    LocalDate today = LocalDate.now();

    if (preferredTime == null) {
      return LocalDateTime.now().plusDays(intervalDays);
    }

    LocalDateTime todayAtPreferredTime = LocalDateTime.of(today, preferredTime);

    if (todayAtPreferredTime.isAfter(LocalDateTime.now())) {
      return todayAtPreferredTime;
    }

    return todayAtPreferredTime.plusDays(intervalDays);
  }

  @Override
  public List<ReminderRule> getRemindersForPlant(Long userId, Long plantId) {
    assertPlantOwnership(userId, plantId);
    return reminderRulePort.findAllByPlantId(plantId);
  }

  @Override
  public void deleteReminder(Long userId, Long reminderId) {
    ReminderRule rule = reminderRulePort.findById(reminderId)
        .orElseThrow(() -> new RuntimeException("Erinnerung nicht gefunden: " + reminderId));

    assertPlantOwnership(userId, rule.getPlantId());
    reminderRulePort.deleteById(reminderId);
  }

  @Override
  public void deactivateReminder(Long userId, Long reminderId) {
    setActiveState(userId, reminderId, false);
  }

  @Override
  public void activateReminder(Long userId, Long reminderId) {
    setActiveState(userId, reminderId, true);
  }

  private void setActiveState(Long userId, Long reminderId, boolean active) {
    ReminderRule rule = reminderRulePort.findById(reminderId)
        .orElseThrow(() -> new RuntimeException("Erinnerung nicht gefunden: " + reminderId));

    assertPlantOwnership(userId, rule.getPlantId());

    ReminderRule updated = new ReminderRule(
        rule.getId(),
        rule.getPlantId(),
        rule.getType(),
        rule.getCustomLabel(),
        rule.getIntervalDays(),
        rule.getPreferredTime(),
        rule.getLastTriggeredAt(),
        rule.getNextDueAt(),
        active
    );

    reminderRulePort.save(updated);
  }

  private void assertPlantOwnership(Long userId, Long plantId) {
    Plant plant = plantPort.findById(plantId)
        .orElseThrow(() -> new RuntimeException("Pflanze nicht gefunden: " + plantId));

    if (!plant.getUserId().equals(userId)) {
      throw new RuntimeException("Keine Berechtigung für diese Pflanze");
    }
  }
}