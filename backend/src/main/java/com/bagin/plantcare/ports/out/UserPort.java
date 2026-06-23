package com.bagin.plantcare.ports.out;

import com.bagin.plantcare.domain.model.User;
import java.util.Optional;

public interface UserPort {
  User save(User user);

  Optional<User> findById(Long id);

  Optional<User> findByEmail(String email);
}
