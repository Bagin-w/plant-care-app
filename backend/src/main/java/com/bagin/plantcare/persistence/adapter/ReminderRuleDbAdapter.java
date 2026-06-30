package com.bagin.plantcare.persistence.adapter;

import com.bagin.plantcare.domain.model.ReminderRule;
import com.bagin.plantcare.persistence.mapper.ReminderRuleMapper;
import com.bagin.plantcare.persistence.repository.ReminderRuleJpaRepository;
import com.bagin.plantcare.ports.out.ReminderRulePort;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReminderRuleDbAdapter implements ReminderRulePort {

  private final ReminderRuleJpaRepository jpaRepository;
  private final ReminderRuleMapper mapper;

  public ReminderRuleDbAdapter(ReminderRuleJpaRepository jpaRepository, ReminderRuleMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public ReminderRule save(ReminderRule reminderRule) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(reminderRule)));
  }

  @Override
  public Optional<ReminderRule> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<ReminderRule> findAllByPlantId(Long plantId) {
    return jpaRepository.findAllByPlantId(plantId).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<ReminderRule> findAllDueByDateTime(LocalDateTime date) {
    return jpaRepository.findAllByActiveTrueAndNextDueAtLessThanEqual(date).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}