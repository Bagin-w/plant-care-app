package com.bagin.plantcare.persistence.tablemodel;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reminder_rules")
@Getter
@Setter
@NoArgsConstructor
public class ReminderRuleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long plantId;

  @Enumerated(EnumType.STRING)
  private ReminderRuleType type;

  private String customLabel;

  private Integer intervalDays;

  private LocalTime preferredTime;

  private LocalDateTime lastTriggeredAt;

  private LocalDateTime nextDueAt;

  private boolean active;
}