package com.bagin.plantcare.rest.controller;

import com.bagin.plantcare.domain.model.ReminderRule;
import com.bagin.plantcare.ports.in.ReminderRuleUseCase;
import com.bagin.plantcare.rest.dto.CreateReminderRequest;
import com.bagin.plantcare.rest.dto.ReminderRuleResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReminderRuleController {

  private final ReminderRuleUseCase reminderRuleUseCase;

  public ReminderRuleController(ReminderRuleUseCase reminderRuleUseCase) {
    this.reminderRuleUseCase = reminderRuleUseCase;
  }

  @PostMapping("/api/plants/{plantId}/reminders")
  public ReminderRuleResponse create(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long plantId,
      @RequestBody CreateReminderRequest request
  ) {
    ReminderRule rule = reminderRuleUseCase.createReminder(
        userId,
        plantId,
        request.type(),
        request.customLabel(),
        request.intervalDays(),
        request.preferredTime()
    );
    return ReminderRuleResponse.fromDomain(rule);
  }

  @GetMapping("/api/plants/{plantId}/reminders")
  public List<ReminderRuleResponse> getAll(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long plantId
  ) {
    return reminderRuleUseCase.getRemindersForPlant(userId, plantId).stream()
        .map(ReminderRuleResponse::fromDomain)
        .toList();
  }

  @PatchMapping("/api/reminders/{reminderId}/deactivate")
  public void deactivate(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long reminderId
  ) {
    reminderRuleUseCase.deactivateReminder(userId, reminderId);
  }

  @DeleteMapping("/api/reminders/{reminderId}")
  public void delete(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long reminderId
  ) {
    reminderRuleUseCase.deleteReminder(userId, reminderId);
  }

  @PatchMapping("/api/reminders/{reminderId}/activate")
  public void activate(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long reminderId
  ) {
    reminderRuleUseCase.activateReminder(userId, reminderId);
  }
}