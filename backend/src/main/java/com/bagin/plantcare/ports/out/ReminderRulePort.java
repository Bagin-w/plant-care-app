package com.bagin.plantcare.ports.out;

import com.bagin.plantcare.domain.model.ReminderRule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRulePort {

  ReminderRule save(ReminderRule reminderRule);

  Optional<ReminderRule> findById(Long id);

  List<ReminderRule> findAllByPlantId(Long plantId);

  List<ReminderRule> findAllDueByDateTime(LocalDateTime date);

  void deleteById(Long id);
}