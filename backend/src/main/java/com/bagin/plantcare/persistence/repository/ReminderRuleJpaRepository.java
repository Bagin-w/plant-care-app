package com.bagin.plantcare.persistence.repository;

import com.bagin.plantcare.persistence.tablemodel.ReminderRuleEntity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRuleJpaRepository extends JpaRepository<ReminderRuleEntity, Long> {

  List<ReminderRuleEntity> findAllByPlantId(Long plantId);

  List<ReminderRuleEntity> findAllByActiveTrueAndNextDueAtLessThanEqual(LocalDateTime date);
}