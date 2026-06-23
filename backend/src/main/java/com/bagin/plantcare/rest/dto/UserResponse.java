package com.bagin.plantcare.rest.dto;

import com.bagin.plantcare.domain.model.User;

public record UserResponse(
    Long id,
    String email,
    String name
) {
  public static UserResponse fromDomain(User user) {
    return new UserResponse(user.getId(), user.getEmail(), user.getName());
  }
}
