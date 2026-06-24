package com.bagin.plantcare.common.scheduling;

import com.bagin.plantcare.domain.model.ReminderRule;
import com.bagin.plantcare.ports.out.ReminderRulePort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReminderScheduler {

  private final ReminderRulePort reminderRulePort;

  public ReminderScheduler(ReminderRulePort reminderRulePort) {
    this.reminderRulePort = reminderRulePort;
  }

  // Sec, Min, Hour, Day, Month, Weekday
  // So 1H: "0 0 * * * *"
  @Scheduled(cron = "0 0 * * * *")
  public void checkDueReminders() {
    LocalDate today = LocalDate.now();
    List<ReminderRule> dueReminders = reminderRulePort.findAllDueByDate(today);

    //TODO
    for (ReminderRule reminder : dueReminders) {
      System.out.println("FÄLLIG: Reminder " + reminder.getId() +
          " für Pflanze " + reminder.getPlantId() +
          " (Typ: " + reminder.getType() + ")");
    }
  }
}