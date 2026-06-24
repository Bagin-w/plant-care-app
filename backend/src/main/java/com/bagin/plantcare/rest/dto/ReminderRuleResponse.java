package com.bagin.plantcare.rest.dto;

import com.bagin.plantcare.domain.model.ReminderRule;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReminderRuleResponse(
    Long id,
    Long plantId,
    ReminderRule.Type type,
    Integer intervalDays,
    LocalTime preferredTime,
    LocalDate lastTriggeredAt,
    LocalDate nextDueAt,
    boolean active
) {
  public static ReminderRuleResponse fromDomain(ReminderRule rule) {
    return new ReminderRuleResponse(
        rule.getId(),
        rule.getPlantId(),
        rule.getType(),
        rule.getIntervalDays(),
        rule.getPreferredTime(),
        rule.getLastTriggeredAt(),
        rule.getNextDueAt(),
        rule.isActive()
    );
  }
}