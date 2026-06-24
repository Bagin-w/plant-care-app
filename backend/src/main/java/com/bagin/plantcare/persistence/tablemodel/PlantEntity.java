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
@Table(name = "plants")
@Getter
@Setter
@NoArgsConstructor
public class PlantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private String nickname;

  private String speciesName;

  @Column(length = 1000)
  private String photoUrl;

  private String location;
}