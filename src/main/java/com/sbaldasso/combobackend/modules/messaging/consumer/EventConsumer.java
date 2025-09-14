package com.sbaldasso.combobackend.modules.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbaldasso.combobackend.modules.delivery.service.DeliveryService;
import com.sbaldasso.combobackend.modules.payment.service.PaymentService;
import com.sbaldasso.combobackend.modules.tracking.service.TrackingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private final ObjectMapper objectMapper;
    private final DeliveryService deliveryService;
    private final PaymentService paymentService;
    private final TrackingService trackingService;

    public EventConsumer(ObjectMapper objectMapper,
                        DeliveryService deliveryService,
                        PaymentService paymentService,
                        TrackingService trackingService) {
        this.objectMapper = objectMapper;
        this.deliveryService = deliveryService;
        this.paymentService = paymentService;
        this.trackingService = trackingService;
    }

    @KafkaListener(topics = "delivery-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeDeliveryEvent(String message) {
        try {
            // Parse and process delivery event
            // Update delivery status, notify users, etc.
            deliveryService.handleDeliveryEvent(message);
        } catch (Exception e) {
            // Log error and handle failure
        }
    }

    @KafkaListener(topics = "payment-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentEvent(String message) {
        try {
            // Parse and process payment event
            // Update payment status, trigger notifications, etc.
            paymentService.handlePaymentEvent(message);
        } catch (Exception e) {
            // Log error and handle failure
        }
    }

    @KafkaListener(topics = "location-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeLocationEvent(String message) {
        try {
            // Parse and process location update
            // Update Redis cache, notify WebSocket clients
            trackingService.handleLocationEvent(message);
        } catch (Exception e) {
            // Log error and handle failure
        }
    }
}
