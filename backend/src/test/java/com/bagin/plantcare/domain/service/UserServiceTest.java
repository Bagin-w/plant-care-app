package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.ports.out.UserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserPort userPort;

  @Mock
  private PasswordEncoder passwordEncoder;

  private UserService userService;

  @BeforeEach
  void setUp() {
    userService = new UserService(userPort, passwordEncoder);
  }

  @Nested
  class RegisterUser {

    @Test
    void hashesPasswordAndSavesNewUserWithoutId() {
      when(passwordEncoder.encode("rawPw")).thenReturn("hashedPw");
      when(userPort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

      User result = userService.registerUser("test@test.de", "rawPw", "Test User");

      ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
      verify(userPort).save(captor.capture());
      User saved = captor.getValue();

      assertThat(saved.getId()).isNull();
      assertThat(saved.getEmail()).isEqualTo("test@test.de");
      assertThat(saved.getPasswordHash()).isEqualTo("hashedPw");
      assertThat(saved.getName()).isEqualTo("Test User");
      assertThat(result).isEqualTo(saved);
    }

    @Test
    void doesNotCheckEmailUniqueness() {
      when(passwordEncoder.encode(any())).thenReturn("hashedPw");
      when(userPort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

      userService.registerUser("test@test.de", "rawPw", "Test User");

      verify(userPort).save(any(User.class));
      verifyNoMoreInteractions(userPort);
    }
  }

  @Nested
  class GetUserById {

    @Test
    void userExists_returnsUser() {
      User user = new User(1L, "test@test.de", "hashedPw", "Test User");
      when(userPort.findById(1L)).thenReturn(Optional.of(user));

      User result = userService.getUserById(1L);

      assertThat(result).isEqualTo(user);
    }

    @Test
    void userMissing_throwsNotFound() {
      when(userPort.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.getUserById(99L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("User nicht gefunden: 99");
    }
  }
}
