package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.ports.in.UserUseCase;
import com.bagin.plantcare.rest.dto.RegisterUserRequest;
import com.bagin.plantcare.rest.dto.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserUseCase userUseCase;

  public UserController(UserUseCase userUseCase) {
    this.userUseCase = userUseCase;
  }

  @PostMapping
  public UserResponse register(@RequestBody RegisterUserRequest request) {
    User createdUser = userUseCase.registerUser(
        request.email(),
        request.password(),
        request.name()
    );
    return UserResponse.fromDomain(createdUser);
  }

  @GetMapping("/{id}")
  public UserResponse getById(@PathVariable Long id) {
    User user = userUseCase.getUserById(id);
    return UserResponse.fromDomain(user);
  }
}
