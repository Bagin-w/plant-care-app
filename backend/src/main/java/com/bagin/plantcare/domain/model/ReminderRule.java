package com.bagin.plantcare.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReminderRule {

  public enum Type {
    WATERING, FERTILIZING, CUSTOM
  }

  private final Long id;
  private final Long plantId;
  private final Type type;
  private final Integer intervalDays;
  private final LocalTime preferredTime;
  private final LocalDate lastTriggeredAt;
  private final LocalDate nextDueAt;
  private final boolean active;

}