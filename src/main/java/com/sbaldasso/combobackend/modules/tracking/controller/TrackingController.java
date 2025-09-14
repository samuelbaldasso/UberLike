package com.sbaldasso.combobackend.modules.tracking.controller;

import com.sbaldasso.combobackend.modules.tracking.dto.LocationUpdateDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/tracking")
public class TrackingController {

    private final SimpMessagingTemplate messagingTemplate;

    public TrackingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/driver/location")
    @SendTo("/topic/delivery/{deliveryId}")
    public LocationUpdateDTO handleDriverLocation(LocationUpdateDTO locationUpdate) {
        // Broadcast driver location to specific delivery topic
        messagingTemplate.convertAndSend(
            "/topic/delivery/" + locationUpdate.getDeliveryId(), 
            locationUpdate
        );
        return locationUpdate;
    }

    @MessageMapping("/driver/status")
    @SendTo("/topic/driver/{driverId}")
    public String handleDriverStatus(String status) {
        return status;
    }
}
