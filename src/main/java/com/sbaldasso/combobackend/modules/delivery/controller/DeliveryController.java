package com.sbaldasso.combobackend.modules.delivery.controller;

import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.delivery.dto.CreateDeliveryRequest;
import com.sbaldasso.combobackend.modules.delivery.dto.DeliveryResponse;
import com.sbaldasso.combobackend.modules.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

  private final DeliveryService deliveryService;

  @PostMapping
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<DeliveryResponse> createDelivery(
      @RequestAttribute UUID userId,
      @Valid @RequestBody CreateDeliveryRequest request) {
    return ResponseEntity.ok(deliveryService.createDelivery(userId, request));
  }

  @PostMapping("/{deliveryId}/accept")
  @PreAuthorize("hasRole('DRIVER')")
  public ResponseEntity<DeliveryResponse> acceptDelivery(
      @PathVariable UUID deliveryId,
      @RequestAttribute UUID userId) {
    return ResponseEntity.ok(deliveryService.acceptDelivery(deliveryId, userId));
  }

  @PutMapping("/{deliveryId}/status")
  @PreAuthorize("hasRole('DRIVER')")
  public ResponseEntity<DeliveryResponse> updateDeliveryStatus(
      @PathVariable UUID deliveryId,
      @RequestAttribute UUID userId,
      @RequestParam DeliveryStatus status) {
    return ResponseEntity.ok(deliveryService.updateDeliveryStatus(deliveryId, userId, status));
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
  public ResponseEntity<Page<DeliveryResponse>> getDeliveries(
      @RequestAttribute UUID userId,
      Pageable pageable) {
    return ResponseEntity.ok(deliveryService.getDeliveriesForUser(userId, pageable));
  }

  @GetMapping("/{deliveryId}")
  @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
  public ResponseEntity<DeliveryResponse> getDelivery(@PathVariable UUID deliveryId) {
    return ResponseEntity.ok(deliveryService.getDeliveryResponseById(deliveryId));
  }
}