package com.sbaldasso.combobackend.modules.notification.service;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.notification.dto.DeliveryStatusMessage;
import com.sbaldasso.combobackend.modules.notification.dto.LocationUpdateMessage;
import com.sbaldasso.combobackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebSocketNotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketNotificationService notificationService;

    @Captor
    private ArgumentCaptor<LocationUpdateMessage> locationCaptor;

    @Captor
    private ArgumentCaptor<DeliveryStatusMessage> statusCaptor;

    private LocationUpdateMessage locationUpdate;
    private Delivery delivery;
    private User customer;
    private User driver;

    @BeforeEach
    void setUp() {
        UUID deliveryId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        locationUpdate = LocationUpdateMessage.builder()
                .deliveryId(deliveryId)
                .driverId(driverId)
                .latitude(-23.550520)
                .longitude(-46.633308)
                .speed(40.0)
                .heading(90.0)
                .build();

        customer = new User();
        customer.setId(UUID.randomUUID());

        driver = new User();
        driver.setId(driverId);

        delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setCustomer(customer);
        delivery.setDriver(driver);
        delivery.setStatus(DeliveryStatus.IN_ROUTE);
    }

    @Test
    void notifyLocationUpdate_ShouldSendToCorrectTopics() {
        // Act
        notificationService.notifyLocationUpdate(locationUpdate);

        // Assert
        verify(messagingTemplate).convertAndSend(
                "/topic/deliveries/" + locationUpdate.getDeliveryId() + "/location",
                locationUpdate
        );

        verify(messagingTemplate).convertAndSend(
                "/topic/drivers/" + locationUpdate.getDriverId() + "/location",
                locationUpdate
        );
    }

    @Test
    void notifyDeliveryStatusChange_ShouldSendToAllParties() {
        // Act
        notificationService.notifyDeliveryStatusChange(delivery);

        // Assert - Captura a mensagem enviada
        verify(messagingTemplate).convertAndSend(
                "/topic/users/" + customer.getId() + "/deliveries",
                statusCaptor.capture()
        );

        verify(messagingTemplate).convertAndSend(
                "/topic/users/" + driver.getId() + "/deliveries",
                statusCaptor.capture()
        );

        verify(messagingTemplate).convertAndSend(
                "/topic/deliveries/" + delivery.getId() + "/status",
                statusCaptor.capture()
        );

        // Verifica o conteúdo das mensagens
        DeliveryStatusMessage capturedMessage = statusCaptor.getValue();
        assertEquals(delivery.getId(), capturedMessage.getDeliveryId());
        assertEquals(delivery.getStatus().toString(), capturedMessage.getStatus());
        assertNotNull(capturedMessage.getTimestamp());
        assertNotNull(capturedMessage.getMessage());
    }

    @Test
    void notifyDeliveryStatusChange_WithoutDriver_ShouldOnlySendToCustomer() {
        // Arrange
        delivery.setDriver(null);

        // Act
        notificationService.notifyDeliveryStatusChange(delivery);

        // Assert
        verify(messagingTemplate).convertAndSend(
                "/topic/users/" + customer.getId() + "/deliveries",
                statusCaptor.capture()
        );

        verify(messagingTemplate).convertAndSend(
                "/topic/deliveries/" + delivery.getId() + "/status",
                statusCaptor.capture()
        );

        DeliveryStatusMessage capturedMessage = statusCaptor.getValue();
        assertEquals(delivery.getId(), capturedMessage.getDeliveryId());
        assertEquals(delivery.getStatus().toString(), capturedMessage.getStatus());
    }
}
