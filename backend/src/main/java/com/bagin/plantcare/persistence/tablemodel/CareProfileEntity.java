package com.bagin.plantcare.persistence.tablemodel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "care_profiles")
@Getter
@Setter
@NoArgsConstructor
public class CareProfileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long plantId;

  private String lightRequirement;

  private Integer temperatureMin;

  private Integer temperatureMax;

  private String humidityRequirement;

  private Integer wateringIntervalDays;

  private Integer fertilizingIntervalDays;

  private String notes;
}