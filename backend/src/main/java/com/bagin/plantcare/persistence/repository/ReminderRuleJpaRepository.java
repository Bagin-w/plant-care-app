package com.bagin.plantcare.persistence.repository;

import com.bagin.plantcare.persistence.tablemodel.ReminderRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReminderRuleJpaRepository extends JpaRepository<ReminderRuleEntity, Long> {

  List<ReminderRuleEntity> findAllByPlantId(Long plantId);

  List<ReminderRuleEntity> findAllByActiveTrueAndNextDueAtLessThanEqual(LocalDate date);
}