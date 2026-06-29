package com.bagin.plantcare.rest.dto;

import com.bagin.plantcare.domain.model.ReminderRule;

import java.time.LocalTime;

public record CreateReminderRequest(
    ReminderRule.Type type,
    String customLabel,
    Integer intervalDays,
    LocalTime preferredTime
) {
}