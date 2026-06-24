package com.bagin.plantcare.rest.dto;

import com.bagin.plantcare.domain.model.ReminderRule;

import java.time.LocalTime;

public record CreateReminderRequest(
    ReminderRule.Type type,
    Integer intervalDays,
    LocalTime preferredTime
) {
}