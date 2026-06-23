package com.bagin.plantcare.persistence.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PerenualSpeciesDto(
    Long id,
    String common_name,
    List<String> scientific_name,
    PerenualImageDto default_image
) {
}