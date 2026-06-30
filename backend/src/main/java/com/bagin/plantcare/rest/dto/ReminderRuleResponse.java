package com.bagin.plantcare.rest.dto;

import com.bagin.plantcare.domain.model.ReminderRule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReminderRuleResponse(
    Long id,
    Long plantId,
    ReminderRule.Type type,
    String customLabel,
    Integer intervalDays,
    LocalTime preferredTime,
    LocalDateTime lastTriggeredAt,
    LocalDateTime nextDueAt,
    boolean active
) {
  public static ReminderRuleResponse fromDomain(ReminderRule rule) {
    return new ReminderRuleResponse(
        rule.getId(),
        rule.getPlantId(),
        rule.getType(),
        rule.getCustomLabel(),
        rule.getIntervalDays(),
        rule.getPreferredTime(),
        rule.getLastTriggeredAt(),
        rule.getNextDueAt(),
        rule.isActive()
    );
  }
}