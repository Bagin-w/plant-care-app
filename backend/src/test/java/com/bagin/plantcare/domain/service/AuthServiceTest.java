package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.ports.out.UserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserPort userPort;

  @Mock
  private PasswordEncoder passwordEncoder;

  private AuthService authService;

  @BeforeEach
  void setUp() {
    authService = new AuthService(userPort, passwordEncoder);
  }

  @Test
  void correctCredentials_returnsUser() {
    User user = new User(1L, "test@test.de", "hashedPw", "Test User");
    when(userPort.findByEmail("test@test.de")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("rawPw", "hashedPw")).thenReturn(true);

    User result = authService.login("test@test.de", "rawPw");

    assertThat(result).isEqualTo(user);
  }

  @Test
  void unknownEmail_throwsGenericInvalidCredentialsError() {
    when(userPort.findByEmail("unknown@test.de")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login("unknown@test.de", "anyPassword"))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Ungültige Anmeldedaten");

    verifyNoInteractions(passwordEncoder);
  }

  @Test
  void wrongPassword_throwsGenericInvalidCredentialsError() {
    User user = new User(1L, "test@test.de", "hashedPw", "Test User");
    when(userPort.findByEmail("test@test.de")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongPw", "hashedPw")).thenReturn(false);

    assertThatThrownBy(() -> authService.login("test@test.de", "wrongPw"))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Ungültige Anmeldedaten");
  }

  @Test
  void wrongEmailAndWrongPassword_produceIdenticalExceptionTypeAndMessage() {
    when(userPort.findByEmail("unknown@test.de")).thenReturn(Optional.empty());
    RuntimeException emailFailure = catchThrowableOfType(
        RuntimeException.class, () -> authService.login("unknown@test.de", "anyPassword"));

    User user = new User(1L, "test@test.de", "hashedPw", "Test User");
    when(userPort.findByEmail("test@test.de")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongPw", "hashedPw")).thenReturn(false);
    RuntimeException passwordFailure = catchThrowableOfType(
        RuntimeException.class, () -> authService.login("test@test.de", "wrongPw"));

    assertThat(emailFailure).isNotNull();
    assertThat(passwordFailure).isNotNull();
    assertThat(emailFailure.getClass()).isEqualTo(passwordFailure.getClass());
    assertThat(emailFailure.getMessage()).isEqualTo(passwordFailure.getMessage());
  }
}
