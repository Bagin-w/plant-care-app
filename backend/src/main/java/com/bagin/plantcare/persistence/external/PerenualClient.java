package com.bagin.plantcare.persistence.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PerenualClient {

  private final RestClient restClient;
  private final String apiKey;

  public PerenualClient(
      @Value("${perenual.api.base-url}") String baseUrl,
      @Value("${perenual.api.key}") String apiKey
  ) {
    this.restClient = RestClient.create(baseUrl);
    this.apiKey = apiKey;
  }

  public PerenualSpeciesListResponse fetchSpeciesList(int page) {
    return restClient.get()
        .uri("/species-list?key={key}&page={page}", apiKey, page)
        .retrieve()
        .body(PerenualSpeciesListResponse.class);
  }
}