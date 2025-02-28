package com.sbaldasso.combobackend.modules.payment.domain;

public enum PaymentStatus {
  PENDING,
  PROCESSING,
  COMPLETED,
  FAILED,
  REFUNDED
}