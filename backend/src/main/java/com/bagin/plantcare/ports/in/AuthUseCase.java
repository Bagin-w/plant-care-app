package com.bagin.plantcare.ports.in;

import com.bagin.plantcare.domain.model.User;

public interface AuthUseCase {
  User login(String email, String rawPassword);
}
