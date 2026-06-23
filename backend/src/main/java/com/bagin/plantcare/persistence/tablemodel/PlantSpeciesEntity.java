package com.bagin.plantcare.persistence.tablemodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plant_species")
@Getter
@Setter
@NoArgsConstructor
public class PlantSpeciesEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long perenualId;

  private String commonName;

  private String scientificName;

  private String defaultWatering;

  private String defaultSunlight;

  @Column(length = 1000)
  private String imageUrl;
}