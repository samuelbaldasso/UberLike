package com.sbaldasso.combobackend.modules.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DeliveryStatusMessage {
    private UUID deliveryId;
    private String status;
    private String message;
    private LocalDateTime timestamp;
}
