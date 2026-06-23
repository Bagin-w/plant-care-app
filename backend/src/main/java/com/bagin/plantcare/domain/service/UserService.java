package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.ports.in.UserUseCase;
import com.bagin.plantcare.ports.out.UserPort;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserUseCase {

  private final UserPort userPort;

  public UserService(UserPort userPort) {
    this.userPort = userPort;
  }

  @Override
  public User registerUser(String email, String rawPassword, String name) {
    User newUser = new User(null, email, rawPassword, name);
    return userPort.save(newUser);
  }

  @Override
  public User getUserById(Long id) {
    return userPort.findById(id)
        .orElseThrow(() -> new RuntimeException("User nicht gefunden: " + id));
  }
}
