package com.sbaldasso.combobackend.modules.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@DirtiesContext
class MessagingServiceTest {

    private static final String DELIVERY_TOPIC = "delivery-updates";
    private static final String LOCATION_TOPIC = "location-updates";
    private static final String NOTIFICATION_TOPIC = "notifications";

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.0"));

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private CompletableFuture<String> messageReceived;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    void sendDeliveryUpdate_shouldPublishToKafka() throws Exception {
        // Arrange
        messageReceived = new CompletableFuture<>();
        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);

        // Act
        messagingService.sendDeliveryUpdate(delivery);

        // Assert
        String receivedMessage = messageReceived.get(5, TimeUnit.SECONDS);
        assertNotNull(receivedMessage);
        assertTrue(receivedMessage.contains("IN_PROGRESS"));
    }

    @Test
    void sendLocationUpdate_shouldPublishToKafka() throws Exception {
        // Arrange
        messageReceived = new CompletableFuture<>();
        LocationUpdate locationUpdate = new LocationUpdate(1L, 40.7128, -74.0060);

        // Act
        messagingService.sendLocationUpdate(locationUpdate);

        // Assert
        String receivedMessage = messageReceived.get(5, TimeUnit.SECONDS);
        assertNotNull(receivedMessage);
        assertTrue(receivedMessage.contains("40.7128"));
        assertTrue(receivedMessage.contains("-74.0060"));
    }

    @Test
    void sendNotification_shouldPublishToKafka() throws Exception {
        // Arrange
        messageReceived = new CompletableFuture<>();
        Notification notification = new Notification("user123", "New delivery request!");

        // Act
        messagingService.sendNotification(notification);

        // Assert
        String receivedMessage = messageReceived.get(5, TimeUnit.SECONDS);
        assertNotNull(receivedMessage);
        assertTrue(receivedMessage.contains("user123"));
        assertTrue(receivedMessage.contains("New delivery request!"));
    }

    @Test
    void multipleMessages_shouldBeProcessedIndependently() throws Exception {
        // Arrange
        CompletableFuture<String> firstMessage = new CompletableFuture<>();
        CompletableFuture<String> secondMessage = new CompletableFuture<>();
        
        Delivery delivery1 = new Delivery();
        delivery1.setId(1L);
        delivery1.setStatus(DeliveryStatus.PICKED_UP);
        
        Delivery delivery2 = new Delivery();
        delivery2.setId(2L);
        delivery2.setStatus(DeliveryStatus.DELIVERED);

        // Act
        messagingService.sendDeliveryUpdate(delivery1);
        messagingService.sendDeliveryUpdate(delivery2);

        // Assert
        String message1 = firstMessage.get(5, TimeUnit.SECONDS);
        String message2 = secondMessage.get(5, TimeUnit.SECONDS);
        
        assertNotNull(message1);
        assertNotNull(message2);
        assertNotEquals(message1, message2);
    }

    @Test
    void invalidMessage_shouldHandleError() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            messagingService.sendDeliveryUpdate(null);
        });
    }

    @KafkaListener(topics = {DELIVERY_TOPIC, LOCATION_TOPIC, NOTIFICATION_TOPIC}, groupId = "test-group")
    public void listen(String message) {
        messageReceived.complete(message);
    }

    static class LocationUpdate {
        private Long driverId;
        private double latitude;
        private double longitude;

        public LocationUpdate(Long driverId, double latitude, double longitude) {
            this.driverId = driverId;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        // Getters and setters
    }

    static class Notification {
        private String userId;
        private String message;

        public Notification(String userId, String message) {
            this.userId = userId;
            this.message = message;
        }

        // Getters and setters
    }
}
