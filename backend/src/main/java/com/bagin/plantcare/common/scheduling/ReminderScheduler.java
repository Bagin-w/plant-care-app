package com.bagin.plantcare.common.scheduling;

import com.bagin.plantcare.domain.model.DeviceToken;
import com.bagin.plantcare.domain.model.Plant;
import com.bagin.plantcare.domain.model.ReminderRule;
import com.bagin.plantcare.persistence.external.PushNotificationSender;
import com.bagin.plantcare.ports.out.DeviceTokenPort;
import com.bagin.plantcare.ports.out.PlantPort;
import com.bagin.plantcare.ports.out.ReminderRulePort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ReminderScheduler {

  private final ReminderRulePort reminderRulePort;
  private final PlantPort plantPort;
  private final DeviceTokenPort deviceTokenPort;
  private final PushNotificationSender pushNotificationSender;

  public ReminderScheduler(
      ReminderRulePort reminderRulePort,
      PlantPort plantPort,
      DeviceTokenPort deviceTokenPort,
      PushNotificationSender pushNotificationSender
  ) {
    this.reminderRulePort = reminderRulePort;
    this.plantPort = plantPort;
    this.deviceTokenPort = deviceTokenPort;
    this.pushNotificationSender = pushNotificationSender;
  }

  @Scheduled(cron = "0 0 * * * *")
  public void checkDueReminders() {
    LocalDate today = LocalDate.now();
    List<ReminderRule> dueReminders = reminderRulePort.findAllDueByDate(today);

    for (ReminderRule reminder : dueReminders) {
      processReminder(reminder);
    }
  }

  private void processReminder(ReminderRule reminder) {
    Optional<Plant> plantOpt = plantPort.findById(reminder.getPlantId());
    if (plantOpt.isEmpty()) {
      return;
    }
    Plant plant = plantOpt.get();

    List<DeviceToken> devices = deviceTokenPort.findAllByUserId(plant.getUserId());

    String message = buildMessage(reminder, plant);

    for (DeviceToken device : devices) {
      pushNotificationSender.send(device.getEndpoint(), device.getP256dh(), device.getAuth(), message);
    }

    updateRuleAfterTrigger(reminder);
  }

  private String buildMessage(ReminderRule reminder, Plant plant) {
    String action = switch (reminder.getType()) {
      case WATERING -> "gegossen werden";
      case FERTILIZING -> "gedüngt werden";
      case CUSTOM -> "gepflegt werden";
    };
    return plant.getNickname() + " sollte heute " + action + "!";
  }

  private void updateRuleAfterTrigger(ReminderRule reminder) {
    LocalDate newNextDueAt = LocalDate.now().plusDays(reminder.getIntervalDays());

    ReminderRule updated = new ReminderRule(
        reminder.getId(),
        reminder.getPlantId(),
        reminder.getType(),
        reminder.getIntervalDays(),
        reminder.getPreferredTime(),
        LocalDate.now(),
        newNextDueAt,
        reminder.isActive()
    );

    reminderRulePort.save(updated);
  }
}