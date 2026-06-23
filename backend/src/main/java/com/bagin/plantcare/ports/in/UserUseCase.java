package com.bagin.plantcare.ports.in;

import com.bagin.plantcare.domain.model.User;

public interface UserUseCase {
  User registerUser(String email, String rawPassword, String name);

  User getUserById(Long id);
}
