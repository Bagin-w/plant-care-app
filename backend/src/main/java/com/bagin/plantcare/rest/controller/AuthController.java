package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.ports.in.AuthUseCase;
import com.bagin.plantcare.rest.dto.LoginRequest;
import com.bagin.plantcare.rest.dto.LoginResponse;
import com.bagin.plantcare.rest.security.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthUseCase authUseCase;
  private final JwtService jwtService;

  public AuthController(AuthUseCase authUseCase, JwtService jwtService) {
    this.authUseCase = authUseCase;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest request) {
    User user = authUseCase.login(request.email(), request.password());
    String token = jwtService.generateToken(user.getId(), user.getEmail());
    return new LoginResponse(token);
  }
}