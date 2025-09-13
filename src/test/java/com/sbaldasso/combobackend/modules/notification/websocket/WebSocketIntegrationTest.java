package com.sbaldasso.combobackend.modules.notification.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbaldasso.combobackend.modules.location.domain.Location;
import com.sbaldasso.combobackend.modules.notification.domain.Notification;
import com.sbaldasso.combobackend.modules.notification.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebSocketService webSocketService;

    private WebSocketStompClient stompClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        
        SockJsClient sockJsClient = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    void whenConnectToWebSocket_thenReceiveLocationUpdates() throws Exception {
        // Arrange
        String wsUrl = String.format("ws://localhost:%d/ws", port);
        CompletableFuture<Location> locationMessage = new CompletableFuture<>();
        UUID deliveryId = UUID.randomUUID();

        StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);

        session.subscribe("/topic/delivery/" + deliveryId + "/location", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Location.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                locationMessage.complete((Location) payload);
            }
        });

        // Act
        Location location = new Location();
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        webSocketService.sendLocationUpdate("delivery/" + deliveryId + "/location", location);

        // Assert
        Location receivedLocation = locationMessage.get(5, TimeUnit.SECONDS);
        assertNotNull(receivedLocation);
        assertEquals(location.getLatitude(), receivedLocation.getLatitude());
        assertEquals(location.getLongitude(), receivedLocation.getLongitude());
    }

    @Test
    void whenConnectToWebSocket_thenReceiveNotifications() throws Exception {
        // Arrange
        String wsUrl = String.format("ws://localhost:%d/ws", port);
        CompletableFuture<Notification> notificationMessage = new CompletableFuture<>();
        UUID userId = UUID.randomUUID();

        StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);

        session.subscribe("/topic/user/" + userId + "/notifications", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Notification.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                notificationMessage.complete((Notification) payload);
            }
        });

        // Act
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Nova entrega")
                .message("Você tem uma nova entrega disponível!")
                .build();

        webSocketService.sendNotification("user/" + userId + "/notifications", notification);

        // Assert
        Notification receivedNotification = notificationMessage.get(5, TimeUnit.SECONDS);
        assertNotNull(receivedNotification);
        assertEquals(notification.getTitle(), receivedNotification.getTitle());
        assertEquals(notification.getMessage(), receivedNotification.getMessage());
    }

    @Test
    void whenSendInvalidMessage_thenHandleError() throws Exception {
        // Arrange
        String wsUrl = String.format("ws://localhost:%d/ws", port);
        CompletableFuture<String> errorMessage = new CompletableFuture<>();
        
        StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);

        session.subscribe("/user/queue/errors", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorMessage.complete((String) payload);
            }
        });

        // Act
        session.send("/app/invalid", "Invalid message");

        // Assert
        String error = errorMessage.get(5, TimeUnit.SECONDS);
        assertNotNull(error);
        assertTrue(error.contains("error"));
    }

    @Test
    void whenSubscribeToInvalidTopic_thenReceiveError() throws Exception {
        // Arrange
        String wsUrl = String.format("ws://localhost:%d/ws", port);
        CompletableFuture<Boolean> connectionError = new CompletableFuture<>();
        
        StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);

        // Act & Assert
        try {
            session.subscribe("/invalid/topic", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Object.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    connectionError.complete(true);
                }
            });
        } catch (Exception e) {
            connectionError.complete(true);
        }

        assertTrue(connectionError.get(5, TimeUnit.SECONDS));
    }
}
