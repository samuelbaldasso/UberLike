package com.sbaldasso.combobackend.modules.messaging.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.messaging.event.DeliveryEvent;
import com.sbaldasso.combobackend.modules.messaging.event.LocationEvent;
import com.sbaldasso.combobackend.modules.messaging.event.PaymentEvent;
import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import com.sbaldasso.combobackend.modules.tracking.dto.LocationUpdateDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String DELIVERY_TOPIC = "delivery-events";
    private static final String PAYMENT_TOPIC = "payment-events";
    private static final String LOCATION_TOPIC = "location-events";

    public MessagingService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendDeliveryEvent(Delivery delivery, String eventType) {
        try {
            DeliveryEvent event = new DeliveryEvent(delivery.getId(), eventType, delivery);
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(DELIVERY_TOPIC, delivery.getId().toString(), message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send delivery event", e);
        }
    }

    public void sendPaymentEvent(Payment payment, String eventType) {
        try {
            PaymentEvent event = new PaymentEvent(payment.getId(), eventType, payment);
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(PAYMENT_TOPIC, payment.getId(), message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send payment event", e);
        }
    }

    public void sendLocationUpdate(LocationUpdateDTO location) {
        try {
            LocationEvent event = new LocationEvent(
                location.getDriverId(),
                "LOCATION_UPDATE",
                location
            );
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(LOCATION_TOPIC, location.getDriverId().toString(), message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send location update", e);
        }
    }
}
