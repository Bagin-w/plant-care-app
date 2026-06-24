package com.bagin.plantcare.rest.dto;

public record CreatePlantRequest(
    String nickname,
    String speciesName,
    String photoUrl,
    String location
) {
}