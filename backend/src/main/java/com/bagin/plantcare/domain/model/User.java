package com.bagin.plantcare.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class User {

  private final Long id;
  private final String email;
  private final String passwordHash;
  private final String name;

}