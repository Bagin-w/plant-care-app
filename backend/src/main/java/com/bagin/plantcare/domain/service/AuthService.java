package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.ports.in.AuthUseCase;
import com.bagin.plantcare.ports.out.UserPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthUseCase {

  private final UserPort userPort;
  private final PasswordEncoder passwordEncoder;

  public AuthService(UserPort userPort, PasswordEncoder passwordEncoder) {
    this.userPort = userPort;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public User login(String email, String rawPassword) {
    User user = userPort.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Ungültige Anmeldedaten"));

    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new RuntimeException("Ungültige Anmeldedaten");
    }

    // Email u passwort beide als ungültige Anmeldedaten angegeben, um keine Informationen über
    // existente Emails zu geben.

    return user;
  }
}
