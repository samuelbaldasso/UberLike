package com.sbaldasso.combobackend.modules.payment.dto;

import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {
    private UUID id;
    private UUID deliveryId;
    private Payment.PaymentStatus status;
    private Payment.PaymentMethod method;
    private BigDecimal amount;
    private BigDecimal driverAmount;
    private BigDecimal platformFee;
    private String transactionId;
    private String pixKey;
    private String cardLastFourDigits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
