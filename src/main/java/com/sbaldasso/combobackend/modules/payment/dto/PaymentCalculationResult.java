package com.sbaldasso.combobackend.modules.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentCalculationResult {
    private BigDecimal totalAmount;
    private BigDecimal driverAmount;
    private BigDecimal platformFee;
}
