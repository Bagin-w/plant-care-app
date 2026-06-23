package com.bagin.plantcare.persistence.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PerenualSpeciesListResponse(
    List<PerenualSpeciesDto> data,
    int current_page,
    int last_page,
    int total
) {
}