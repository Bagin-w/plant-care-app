package com.bagin.plantcare.persistence.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PerenualImageDto(
    String regular_url,
    String thumbnail
) {
}