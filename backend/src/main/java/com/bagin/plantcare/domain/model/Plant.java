package com.bagin.plantcare.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Plant {

  private final Long id;
  private final Long userId;
  private final String nickname;
  private final String speciesName;
  private final String photoUrl;
  private final String location;

}
