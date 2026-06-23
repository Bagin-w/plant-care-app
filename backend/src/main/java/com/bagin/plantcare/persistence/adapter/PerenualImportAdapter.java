package com.bagin.plantcare.persistence.adapter;

import com.bagin.plantcare.domain.model.PlantSpecies;
import com.bagin.plantcare.persistence.external.PerenualClient;
import com.bagin.plantcare.persistence.external.PerenualSpeciesDto;
import com.bagin.plantcare.persistence.external.PerenualSpeciesListResponse;
import com.bagin.plantcare.ports.out.PlantSpeciesPort;
import org.springframework.stereotype.Component;

@Component
public class PerenualImportAdapter {

  private final PerenualClient perenualClient;
  private final PlantSpeciesPort plantSpeciesPort;

  public PerenualImportAdapter(PerenualClient perenualClient, PlantSpeciesPort plantSpeciesPort) {
    this.perenualClient = perenualClient;
    this.plantSpeciesPort = plantSpeciesPort;
  }

  public int importPage(int page) {
    PerenualSpeciesListResponse response = perenualClient.fetchSpeciesList(page);

    int importedCount = 0;
    for (PerenualSpeciesDto dto : response.data()) {
      boolean alreadyExists = plantSpeciesPort.findByPerenualId(dto.id()).isPresent();

      if (!alreadyExists) {
        PlantSpecies species = mapToDomain(dto);
        plantSpeciesPort.save(species);
        importedCount++;
      }
    }

    return importedCount;
  }

  private PlantSpecies mapToDomain(PerenualSpeciesDto dto) {
    String scientificName = (dto.scientific_name() != null && !dto.scientific_name().isEmpty())
        ? dto.scientific_name().get(0)
        : null;

    String imageUrl = (dto.default_image() != null)
        ? dto.default_image().regular_url()
        : null;

    return new PlantSpecies(
        null,
        dto.id(),
        dto.common_name(),
        scientificName,
        null,
        null,
        imageUrl
    );
  }
}