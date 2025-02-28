package com.sbaldasso.combobackend.modules.delivery.domain;

import com.sbaldasso.combobackend.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Delivery {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "customer_id", nullable = false)
  private User customer;

  @ManyToOne
  @JoinColumn(name = "driver_id")
  private User driver;

  @Column(nullable = false)
  private String pickupAddress;

  @Column(nullable = false)
  private String deliveryAddress;

  @Column(nullable = false)
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeliveryStatus status;

  private String description;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  private LocalDateTime pickedUpAt;

  private LocalDateTime deliveredAt;

  @Column(name = "estimated_delivery_time")
  private LocalDateTime estimatedDeliveryTime;
}