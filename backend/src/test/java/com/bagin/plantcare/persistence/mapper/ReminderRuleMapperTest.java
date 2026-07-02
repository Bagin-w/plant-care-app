package com.bagin.plantcare.persistence.mapper;

import com.bagin.plantcare.domain.model.ReminderRule;
import com.bagin.plantcare.persistence.tablemodel.ReminderRuleEntity;
import com.bagin.plantcare.persistence.tablemodel.ReminderRuleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderRuleMapperTest {

  private final ReminderRuleMapper mapper = new ReminderRuleMapper();

  /**
   * ReminderRule.Type (domain) and ReminderRuleType (JPA) are two independently declared
   * enums kept in sync only by matching constant names. The mapper round-trips between them
   * via name()/valueOf(), which compiles fine even if the enums drift apart and only fails
   * at runtime. This test pins every domain constant to a JPA counterpart so such drift is
   * caught here instead of in production.
   */
  @ParameterizedTest
  @EnumSource(ReminderRule.Type.class)
  void everyDomainTypeRoundTripsThroughEntityTypeAndBack(ReminderRule.Type domainType) {
    ReminderRuleEntity entity = mapper.toEntity(fullReminderRule(domainType));
    assertThat(entity.getType().name()).isEqualTo(domainType.name());

    ReminderRule roundTripped = mapper.toDomain(entity);
    assertThat(roundTripped.getType()).isEqualTo(domainType);
  }

  @Test
  void toDomain_mapsEveryFieldFromEntity() {
    LocalTime preferredTime = LocalTime.of(9, 30);
    LocalDateTime lastTriggeredAt = LocalDateTime.of(2026, 1, 1, 9, 30);
    LocalDateTime nextDueAt = LocalDateTime.of(2026, 1, 8, 9, 30);

    ReminderRuleEntity entity = new ReminderRuleEntity();
    entity.setId(1L);
    entity.setPlantId(5L);
    entity.setType(ReminderRuleType.CUSTOM);
    entity.setCustomLabel("Umtopfen");
    entity.setIntervalDays(7);
    entity.setPreferredTime(preferredTime);
    entity.setLastTriggeredAt(lastTriggeredAt);
    entity.setNextDueAt(nextDueAt);
    entity.setActive(true);

    ReminderRule domain = mapper.toDomain(entity);

    assertThat(domain.getId()).isEqualTo(1L);
    assertThat(domain.getPlantId()).isEqualTo(5L);
    assertThat(domain.getType()).isEqualTo(ReminderRule.Type.CUSTOM);
    assertThat(domain.getCustomLabel()).isEqualTo("Umtopfen");
    assertThat(domain.getIntervalDays()).isEqualTo(7);
    assertThat(domain.getPreferredTime()).isEqualTo(preferredTime);
    assertThat(domain.getLastTriggeredAt()).isEqualTo(lastTriggeredAt);
    assertThat(domain.getNextDueAt()).isEqualTo(nextDueAt);
    assertThat(domain.isActive()).isTrue();
  }

  @Test
  void toEntity_mapsEveryFieldFromDomain() {
    ReminderRule domain = fullReminderRule(ReminderRule.Type.WATERING);

    ReminderRuleEntity entity = mapper.toEntity(domain);

    assertThat(entity.getId()).isEqualTo(domain.getId());
    assertThat(entity.getPlantId()).isEqualTo(domain.getPlantId());
    assertThat(entity.getType()).isEqualTo(ReminderRuleType.WATERING);
    assertThat(entity.getCustomLabel()).isEqualTo(domain.getCustomLabel());
    assertThat(entity.getIntervalDays()).isEqualTo(domain.getIntervalDays());
    assertThat(entity.getPreferredTime()).isEqualTo(domain.getPreferredTime());
    assertThat(entity.getLastTriggeredAt()).isEqualTo(domain.getLastTriggeredAt());
    assertThat(entity.getNextDueAt()).isEqualTo(domain.getNextDueAt());
    assertThat(entity.isActive()).isEqualTo(domain.isActive());
  }

  private ReminderRule fullReminderRule(ReminderRule.Type type) {
    return new ReminderRule(
        1L,
        5L,
        type,
        "Custom Label",
        7,
        LocalTime.of(9, 30),
        LocalDateTime.of(2026, 1, 1, 9, 30),
        LocalDateTime.of(2026, 1, 8, 9, 30),
        true
    );
  }
}
