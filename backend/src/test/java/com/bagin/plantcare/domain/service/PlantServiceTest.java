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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlantServiceTest {

  @Mock
  private PlantPort plantPort;

  @Mock
  private CareProfilePort careProfilePort;

  private PlantService plantService;

  private static final Long USER_ID = 1L;

  @BeforeEach
  void setUp() {
    plantService = new PlantService(plantPort, careProfilePort);
  }

  @Nested
  class CreatePlant {

    @Test
    void createsPlantAndAutoProvisionsEmptyCareProfile() {
      Plant savedPlant = new Plant(10L, USER_ID, "Ficus", "Ficus lyrata", "photo.jpg", "Wohnzimmer");
      when(plantPort.save(any(Plant.class))).thenReturn(savedPlant);
      when(careProfilePort.save(any(CareProfile.class))).thenAnswer(inv -> inv.getArgument(0));

      Plant result = plantService.createPlant(USER_ID, "Ficus", "Ficus lyrata", "photo.jpg", "Wohnzimmer");

      assertThat(result).isEqualTo(savedPlant);

      ArgumentCaptor<Plant> plantCaptor = ArgumentCaptor.forClass(Plant.class);
      verify(plantPort).save(plantCaptor.capture());
      Plant plantToSave = plantCaptor.getValue();
      assertThat(plantToSave.getId()).isNull();
      assertThat(plantToSave.getUserId()).isEqualTo(USER_ID);
      assertThat(plantToSave.getNickname()).isEqualTo("Ficus");

      ArgumentCaptor<CareProfile> careProfileCaptor = ArgumentCaptor.forClass(CareProfile.class);
      verify(careProfilePort).save(careProfileCaptor.capture());
      CareProfile profileToSave = careProfileCaptor.getValue();
      assertThat(profileToSave.getId()).isNull();
      assertThat(profileToSave.getPlantId()).isEqualTo(savedPlant.getId());
      assertThat(profileToSave.getLightRequirement()).isNull();
      assertThat(profileToSave.getWateringIntervalDays()).isNull();
      assertThat(profileToSave.getFertilizingIntervalDays()).isNull();
    }

    @Test
    void blankNickname_throwsAndSkipsPersistence() {
      assertThatThrownBy(() -> plantService.createPlant(USER_ID, "   ", "Species", null, null))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Ein Name für die Pflanze ist erforderlich");

      verifyNoInteractions(plantPort, careProfilePort);
    }

    @Test
    void nullNickname_throwsAndSkipsPersistence() {
      assertThatThrownBy(() -> plantService.createPlant(USER_ID, null, "Species", null, null))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Ein Name für die Pflanze ist erforderlich");

      verifyNoInteractions(plantPort, careProfilePort);
    }
  }

  @Nested
  class GetPlantById {

    @Test
    void ownerMatches_returnsPlant() {
      Plant plant = new Plant(5L, USER_ID, "Ficus", null, null, null);
      when(plantPort.findById(5L)).thenReturn(Optional.of(plant));

      Plant result = plantService.getPlantById(USER_ID, 5L);

      assertThat(result).isEqualTo(plant);
    }

    @Test
    void plantMissing_throwsNotFound() {
      when(plantPort.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> plantService.getPlantById(USER_ID, 99L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Pflanze nicht gefunden: 99");
    }

    @Test
    void differentOwner_throwsUnauthorized() {
      Plant plant = new Plant(5L, USER_ID, "Ficus", null, null, null);
      when(plantPort.findById(5L)).thenReturn(Optional.of(plant));

      assertThatThrownBy(() -> plantService.getPlantById(2L, 5L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Keine Berechtigung für diese Pflanze");
    }
  }

  @Test
  void getPlantsForUser_delegatesToPort() {
    List<Plant> plants = List.of(new Plant(1L, USER_ID, "A", null, null, null));
    when(plantPort.findAllByUserId(USER_ID)).thenReturn(plants);

    List<Plant> result = plantService.getPlantsForUser(USER_ID);

    assertThat(result).isEqualTo(plants);
  }

  @Nested
  class DeletePlant {

    @Test
    void ownerMatches_deletesCareProfileBeforePlant() {
      Plant plant = new Plant(5L, USER_ID, "Ficus", null, null, null);
      when(plantPort.findById(5L)).thenReturn(Optional.of(plant));

      plantService.deletePlant(USER_ID, 5L);

      var order = inOrder(careProfilePort, plantPort);
      order.verify(careProfilePort).deleteByPlantId(5L);
      order.verify(plantPort).deleteById(5L);
    }

    @Test
    void plantMissing_throwsAndSkipsDeletion() {
      when(plantPort.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> plantService.deletePlant(USER_ID, 99L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Pflanze nicht gefunden: 99");

      verify(careProfilePort, never()).deleteByPlantId(anyLong());
      verify(plantPort, never()).deleteById(anyLong());
    }

    @Test
    void differentOwner_throwsAndSkipsDeletion() {
      Plant plant = new Plant(5L, USER_ID, "Ficus", null, null, null);
      when(plantPort.findById(5L)).thenReturn(Optional.of(plant));

      assertThatThrownBy(() -> plantService.deletePlant(2L, 5L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Keine Berechtigung für diese Pflanze");

      verify(careProfilePort, never()).deleteByPlantId(anyLong());
      verify(plantPort, never()).deleteById(anyLong());
    }
  }

  @Nested
  class UpdatePlant {

    @Test
    void ownerMatches_savesUpdatedPlant() {
      Plant existing = new Plant(5L, USER_ID, "OldName", null, null, null);
      when(plantPort.findById(5L)).thenReturn(Optional.of(existing));
      when(plantPort.save(any(Plant.class))).thenAnswer(inv -> inv.getArgument(0));

      Plant result = plantService.updatePlant(USER_ID, 5L, "NewName", "Species", "photo.jpg", "Loc");

      assertThat(result.getId()).isEqualTo(5L);
      assertThat(result.getUserId()).isEqualTo(USER_ID);
      assertThat(result.getNickname()).isEqualTo("NewName");
      assertThat(result.getSpeciesName()).isEqualTo("Species");
      assertThat(result.getPhotoUrl()).isEqualTo("photo.jpg");
      assertThat(result.getLocation()).isEqualTo("Loc");
    }

    @Test
    void plantMissing_throwsNotFound() {
      when(plantPort.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> plantService.updatePlant(USER_ID, 99L, "Name", null, null, null))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Pflanze nicht gefunden: 99");

      verify(plantPort, never()).save(any());
    }

    @Test
    void differentOwner_throwsUnauthorized() {
      Plant existing = new Plant(5L, USER_ID, "OldName", null, null, null);
      when(plantPort.findById(5L)).thenReturn(Optional.of(existing));

      assertThatThrownBy(() -> plantService.updatePlant(2L, 5L, "Name", null, null, null))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Keine Berechtigung für diese Pflanze");

      verify(plantPort, never()).save(any());
    }

    @Test
    void blankNickname_throwsAndSkipsSave() {
      Plant existing = new Plant(5L, USER_ID, "OldName", null, null, null);
      when(plantPort.findById(5L)).thenReturn(Optional.of(existing));

      assertThatThrownBy(() -> plantService.updatePlant(USER_ID, 5L, "  ", null, null, null))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Ein Name für die Pflanze ist erforderlich");

      verify(plantPort, never()).save(any());
    }
  }
}
