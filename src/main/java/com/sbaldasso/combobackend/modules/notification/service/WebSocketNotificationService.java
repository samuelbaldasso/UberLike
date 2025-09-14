package com.sbaldasso.combobackend.modules.notification.service;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.notification.dto.DeliveryStatusMessage;
import com.sbaldasso.combobackend.modules.notification.dto.LocationUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyLocationUpdate(LocationUpdateMessage update) {
        // Envia atualização de localização para o tópico específico da entrega
        messagingTemplate.convertAndSend(
            "/topic/deliveries/" + update.getDeliveryId() + "/location",
            update
        );

        // Envia atualização para o tópico geral do motorista
        messagingTemplate.convertAndSend(
            "/topic/drivers/" + update.getDriverId() + "/location",
            update
        );
    }

    public void notifyDeliveryStatusChange(Delivery delivery) {
        DeliveryStatusMessage message = DeliveryStatusMessage.builder()
            .deliveryId(delivery.getId())
            .status(delivery.getStatus().toString())
            .message(getStatusMessage(delivery.getStatus()))
            .timestamp(LocalDateTime.now())
            .build();

        // Notifica cliente
        messagingTemplate.convertAndSend(
            "/topic/users/" + delivery.getCustomer().getId() + "/deliveries",
            message
        );

        // Notifica motorista
        if (delivery.getDriver() != null) {
            messagingTemplate.convertAndSend(
                "/topic/users/" + delivery.getDriver().getId() + "/deliveries",
                message
            );
        }

        // Tópico geral da entrega
        messagingTemplate.convertAndSend(
            "/topic/deliveries/" + delivery.getId() + "/status",
            message
        );
    }

    private String getStatusMessage(DeliveryStatus status) {
        return switch (status) {
            case CREATED -> "Entrega criada, buscando motorista...";
            case ACCEPTED -> "Motorista aceitou a entrega";
            case COLLECTING -> "Motorista está a caminho do ponto de coleta";
            case IN_ROUTE -> "Motorista está a caminho do destino";
            case DELIVERED -> "Entrega realizada com sucesso";
            case FINISHED -> "Entrega finalizada";
            case CANCELLED -> "Entrega cancelada";
        };
    }
}
