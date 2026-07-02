package com.bagin.plantcare.domain.service;

import com.bagin.plantcare.domain.model.CareProfile;
import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.ports.out.CareProfilePort;
import com.bagin.plantcare.ports.out.PlantPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareProfileServiceTest {

  @Mock
  private CareProfilePort careProfilePort;

  @Mock
  private PlantPort plantPort;

  private CareProfileService careProfileService;

  private static final Long USER_ID = 1L;
  private static final Long PLANT_ID = 5L;

  @BeforeEach
  void setUp() {
    careProfileService = new CareProfileService(careProfilePort, plantPort);
  }

  private void ownedPlantExists() {
    Plant plant = new Plant(PLANT_ID, USER_ID, "Ficus", null, null, null);
    when(plantPort.findById(PLANT_ID)).thenReturn(Optional.of(plant));
  }

  @Nested
  class GetByPlantId {

    @Test
    void ownerMatchesAndProfileExists_returnsProfile() {
      ownedPlantExists();
      CareProfile profile = new CareProfile(2L, PLANT_ID, "hell", 15, 25, "mittel", 7, 30, "Notizen");
      when(careProfilePort.findByPlantId(PLANT_ID)).thenReturn(Optional.of(profile));

      CareProfile result = careProfileService.getByPlantId(USER_ID, PLANT_ID);

      assertThat(result).isEqualTo(profile);
    }

    @Test
    void plantMissing_throwsPlantNotFound() {
      when(plantPort.findById(PLANT_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> careProfileService.getByPlantId(USER_ID, PLANT_ID))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Pflanze nicht gefunden: " + PLANT_ID);
    }

    @Test
    void differentOwner_throwsUnauthorized() {
      Plant plant = new Plant(PLANT_ID, USER_ID, "Ficus", null, null, null);
      when(plantPort.findById(PLANT_ID)).thenReturn(Optional.of(plant));

      assertThatThrownBy(() -> careProfileService.getByPlantId(2L, PLANT_ID))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Keine Berechtigung für diese Pflanze");
    }

    @Test
    void profileMissing_throwsProfileNotFound() {
      ownedPlantExists();
      when(careProfilePort.findByPlantId(PLANT_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> careProfileService.getByPlantId(USER_ID, PLANT_ID))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Pflegeprofil nicht gefunden für Pflanze: " + PLANT_ID);
    }
  }

  @Nested
  class UpdateCareProfile {

    @Test
    void ownerMatches_savesUpdatedProfilePreservingId() {
      ownedPlantExists();
      CareProfile existing = new CareProfile(2L, PLANT_ID, "dunkel", 10, 20, "niedrig", 3, 14, "alt");
      when(careProfilePort.findByPlantId(PLANT_ID)).thenReturn(Optional.of(existing));
      when(careProfilePort.save(any(CareProfile.class))).thenAnswer(inv -> inv.getArgument(0));

      CareProfile result = careProfileService.updateCareProfile(
          USER_ID, PLANT_ID, "hell", 15, 25, "mittel", 7, 30, "neu");

      ArgumentCaptor<CareProfile> captor = ArgumentCaptor.forClass(CareProfile.class);
      verify(careProfilePort).save(captor.capture());
      CareProfile saved = captor.getValue();

      assertThat(saved.getId()).isEqualTo(2L);
      assertThat(saved.getPlantId()).isEqualTo(PLANT_ID);
      assertThat(saved.getLightRequirement()).isEqualTo("hell");
      assertThat(saved.getTemperatureMin()).isEqualTo(15);
      assertThat(saved.getTemperatureMax()).isEqualTo(25);
      assertThat(saved.getHumidityRequirement()).isEqualTo("mittel");
      assertThat(saved.getWateringIntervalDays()).isEqualTo(7);
      assertThat(saved.getFertilizingIntervalDays()).isEqualTo(30);
      assertThat(saved.getNotes()).isEqualTo("neu");
      assertThat(result).isEqualTo(saved);
    }

    @Test
    void plantMissing_throwsAndSkipsSave() {
      when(plantPort.findById(PLANT_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> careProfileService.updateCareProfile(
          USER_ID, PLANT_ID, "hell", 15, 25, "mittel", 7, 30, "neu"))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Pflanze nicht gefunden: " + PLANT_ID);

      verify(careProfilePort, never()).save(any());
    }

    @Test
    void differentOwner_throwsAndSkipsSave() {
      Plant plant = new Plant(PLANT_ID, USER_ID, "Ficus", null, null, null);
      when(plantPort.findById(PLANT_ID)).thenReturn(Optional.of(plant));

      assertThatThrownBy(() -> careProfileService.updateCareProfile(
          2L, PLANT_ID, "hell", 15, 25, "mittel", 7, 30, "neu"))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Keine Berechtigung für diese Pflanze");

      verify(careProfilePort, never()).save(any());
    }

    @Test
    void profileMissing_throwsAndSkipsSave() {
      ownedPlantExists();
      when(careProfilePort.findByPlantId(PLANT_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> careProfileService.updateCareProfile(
          USER_ID, PLANT_ID, "hell", 15, 25, "mittel", 7, 30, "neu"))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Pflegeprofil nicht gefunden für Pflanze: " + PLANT_ID);

      verify(careProfilePort, never()).save(any());
    }
  }
}
