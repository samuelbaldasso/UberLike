package com.sbaldasso.combobackend.modules.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDeliveryRequest {
  @NotBlank(message = "Pickup address is required")
  private String pickupAddress;

  @NotBlank(message = "Delivery address is required")
  private String deliveryAddress;

  @NotNull(message = "Price is required")
  @Positive(message = "Price must be positive")
  private BigDecimal price;

  private String description;
}