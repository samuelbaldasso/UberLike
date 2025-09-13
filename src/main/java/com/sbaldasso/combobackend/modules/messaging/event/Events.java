package com.sbaldasso.combobackend.modules.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryEvent {
    private Long deliveryId;
    private String eventType;
    private Object payload;
}

@Data
@AllArgsConstructor
public class PaymentEvent {
    private String paymentId;
    private String eventType;
    private Object payload;
}

@Data
@AllArgsConstructor
public class LocationEvent {
    private Long driverId;
    private String eventType;
    private Object payload;
}
