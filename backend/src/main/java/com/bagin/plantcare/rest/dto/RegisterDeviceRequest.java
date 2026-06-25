package com.bagin.plantcare.rest.dto;

public record RegisterDeviceRequest(
    String endpoint,
    String p256dh,
    String auth
) {
}