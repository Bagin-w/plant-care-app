package com.bagin.plantcare.rest.dto;

public record RegisterUserRequest(
    String email,
    String password,
    String name
) {
}
