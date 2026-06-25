package com.bagin.plantcare.persistence.external;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Security;

@Component
public class PushNotificationSender {

  private final PushService pushService;

  public PushNotificationSender(
      @Value("${vapid.public.key}") String publicKey,
      @Value("${vapid.private.key}") String privateKey,
      @Value("${vapid.subject}") String subject
  ) throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    this.pushService = new PushService(publicKey, privateKey, subject);
  }

  public void send(String endpoint, String p256dh, String auth, String message) {
    try {
      Notification notification = new Notification(endpoint, p256dh, auth, message);
      pushService.send(notification);
    } catch (Exception e) {
      System.err.println("Push-Versand fehlgeschlagen: " + e.getMessage());
    }
  }
}