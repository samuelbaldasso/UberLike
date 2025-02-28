package com.sbaldasso.combobackend.modules.delivery.dto;

import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DeliveryResponse {
  private UUID id;
  private UUID customerId;
  private UUID driverId;
  private String pickupAddress;
  private String deliveryAddress;
  private BigDecimal price;
  private DeliveryStatus status;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime pickedUpAt;
  private LocalDateTime deliveredAt;
  private LocalDateTime estimatedDeliveryTime;
  private Double driverLatitude;
  private Double driverLongitude;
}