package com.sbaldasso.combobackend.modules.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

  private final SimpMessagingTemplate messagingTemplate;

  public void sendLocationUpdate(String driverId, Object locationUpdate) {
    messagingTemplate.convertAndSend("/topic/location/" + driverId, locationUpdate);
  }

  public void sendDeliveryUpdate(String userId, Object deliveryUpdate) {
    messagingTemplate.convertAndSend("/topic/delivery/" + userId, deliveryUpdate);
  }

  public void sendNotification(String userId, Object notification) {
    messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
  }
}