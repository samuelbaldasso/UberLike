package com.sbaldasso.combobackend.modules.notification.controller;

import com.sbaldasso.combobackend.modules.notification.dto.LocationUpdateMessage;
import com.sbaldasso.combobackend.modules.notification.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final WebSocketNotificationService notificationService;

    @MessageMapping("/location")
    @PreAuthorize("hasRole('DRIVER')")
    public void handleLocationUpdate(@Payload LocationUpdateMessage location) {
        location.setTimestamp(LocalDateTime.now());
        notificationService.notifyLocationUpdate(location);
    }
}
