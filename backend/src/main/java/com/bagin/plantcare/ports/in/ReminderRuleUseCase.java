package com.bagin.plantcare.ports.in;

import com.bagin.plantcare.domain.model.ReminderRule;

import java.time.LocalTime;
import java.util.List;

public interface ReminderRuleUseCase {

  ReminderRule createReminder(
      Long userId,
      Long plantId,
      ReminderRule.Type type,
      String customLabel,
      Integer intervalDays,
      LocalTime preferredTime
  );

  List<ReminderRule> getRemindersForPlant(Long userId, Long plantId);

  void deleteReminder(Long userId, Long reminderId);

  void deactivateReminder(Long userId, Long reminderId);

  void activateReminder(Long userId, Long reminderId);
}