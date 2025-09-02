package com.sbaldasso.combobackend.modules.delivery.controller;

import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.delivery.dto.CreateDeliveryRequest;
import com.sbaldasso.combobackend.modules.delivery.dto.DeliveryResponse;
import com.sbaldasso.combobackend.modules.delivery.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryControllerTest {
  @Mock
  private DeliveryService deliveryService;
  @InjectMocks
  private DeliveryController deliveryController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createDelivery_returnsDeliveryResponse() {
    UUID userId = UUID.randomUUID();
    CreateDeliveryRequest request = new CreateDeliveryRequest();
    DeliveryResponse response = new DeliveryResponse();
    when(deliveryService.createDelivery(userId, request)).thenReturn(response);
    ResponseEntity<DeliveryResponse> result = deliveryController.createDelivery(userId, request);
    assertEquals(response, result.getBody());
  }

  @Test
  void acceptDelivery_returnsDeliveryResponse() {
    UUID deliveryId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    DeliveryResponse response = new DeliveryResponse();
    when(deliveryService.acceptDelivery(deliveryId, userId)).thenReturn(response);
    ResponseEntity<DeliveryResponse> result = deliveryController.acceptDelivery(deliveryId, userId);
    assertEquals(response, result.getBody());
  }

  @Test
  void updateDeliveryStatus_returnsDeliveryResponse() {
    UUID deliveryId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    DeliveryStatus status = DeliveryStatus.PICKED_UP;
    DeliveryResponse response = new DeliveryResponse();
    when(deliveryService.updateDeliveryStatus(deliveryId, userId, status)).thenReturn(response);
    ResponseEntity<DeliveryResponse> result = deliveryController.updateDeliveryStatus(deliveryId, userId, status);
    assertEquals(response, result.getBody());
  }

  @Test
  void getDeliveries_returnsPage() {
    UUID userId = UUID.randomUUID();
    Page<DeliveryResponse> page = new PageImpl<>(Collections.emptyList());
    when(deliveryService.getDeliveriesForUser(userId, Pageable.unpaged())).thenReturn(page);
    ResponseEntity<Page<DeliveryResponse>> result = deliveryController.getDeliveries(userId, Pageable.unpaged());
    assertEquals(page, result.getBody());
  }

  @Test
  void getDelivery_returnsDeliveryResponse() {
    UUID deliveryId = UUID.randomUUID();
    DeliveryResponse response = new DeliveryResponse();
    when(deliveryService.getDeliveryResponseById(deliveryId)).thenReturn(response);
    ResponseEntity<DeliveryResponse> result = deliveryController.getDelivery(deliveryId);
    assertEquals(response, result.getBody());
  }
}
