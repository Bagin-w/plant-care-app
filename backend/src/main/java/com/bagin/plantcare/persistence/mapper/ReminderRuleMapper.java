package com.bagin.plantcare.persistence.mapper;

import com.bagin.plantcare.domain.model.ReminderRule;
import com.bagin.plantcare.persistence.tablemodel.ReminderRuleEntity;
import com.bagin.plantcare.persistence.tablemodel.ReminderRuleType;
import org.springframework.stereotype.Component;

@Component
public class ReminderRuleMapper {

  public ReminderRule toDomain(ReminderRuleEntity entity) {
    return new ReminderRule(
        entity.getId(),
        entity.getPlantId(),
        ReminderRule.Type.valueOf(entity.getType().name()),
        entity.getCustomLabel(),
        entity.getIntervalDays(),
        entity.getPreferredTime(),
        entity.getLastTriggeredAt(),
        entity.getNextDueAt(),
        entity.isActive()
    );
  }

  public ReminderRuleEntity toEntity(ReminderRule reminderRule) {
    ReminderRuleEntity entity = new ReminderRuleEntity();
    entity.setId(reminderRule.getId());
    entity.setPlantId(reminderRule.getPlantId());
    entity.setType(ReminderRuleType.valueOf(reminderRule.getType().name()));
    entity.setCustomLabel(reminderRule.getCustomLabel());
    entity.setIntervalDays(reminderRule.getIntervalDays());
    entity.setPreferredTime(reminderRule.getPreferredTime());
    entity.setLastTriggeredAt(reminderRule.getLastTriggeredAt());
    entity.setNextDueAt(reminderRule.getNextDueAt());
    entity.setActive(reminderRule.isActive());
    return entity;
  }
}