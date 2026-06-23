package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.ports.in.UserUseCase;
import com.bagin.plantcare.ports.out.UserPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserUseCase {

  private final UserPort userPort;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserPort userPort, PasswordEncoder passwordEncoder) {
    this.userPort = userPort;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public User registerUser(String email, String rawPassword, String name) {
    String hashedPassword = passwordEncoder.encode(rawPassword);
    User newUser = new User(null, email, hashedPassword, name);
    return userPort.save(newUser);
  }

  @Override
  public User getUserById(Long id) {
    return userPort.findById(id)
        .orElseThrow(() -> new RuntimeException("User nicht gefunden: " + id));
  }
}
