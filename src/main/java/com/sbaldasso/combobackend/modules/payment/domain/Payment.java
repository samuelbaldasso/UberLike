package com.sbaldasso.combobackend.modules.payment.domain;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "delivery_id", nullable = false)
  private Delivery delivery;

  @Column(nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentMethod method;

  private String transactionId;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  private LocalDateTime paidAt;

  private String failureReason;
}