package com.sbaldasso.combobackend.modules.payment.service;

import com.sbaldasso.combobackend.modules.payment.config.PaymentConfig;
import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import com.sbaldasso.combobackend.modules.payment.dto.PaymentCalculationResult;
import com.sbaldasso.combobackend.modules.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PaymentCalculationService {
    
    private final PaymentConfig paymentConfig;
    
    public PaymentCalculationResult calculatePayment(double distanceInKm, int estimatedTimeInMinutes) {
        // Calcula o valor base pela distância
        BigDecimal distanceAmount = BigDecimal.valueOf(distanceInKm)
                .multiply(paymentConfig.getPricePerKm());

        // Calcula o valor pelo tempo
        BigDecimal timeAmount = BigDecimal.valueOf(estimatedTimeInMinutes)
                .multiply(paymentConfig.getPricePerMinute());

        // Soma os valores e aplica a taxa mínima se necessário
        BigDecimal totalAmount = distanceAmount.add(timeAmount);
        if (totalAmount.compareTo(paymentConfig.getMinimumFee()) < 0) {
            totalAmount = paymentConfig.getMinimumFee();
        }

        // Calcula a taxa da plataforma
        BigDecimal platformFee = totalAmount.multiply(paymentConfig.getPlatformFeePercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Calcula o valor do motorista
        BigDecimal driverAmount = totalAmount.subtract(platformFee);

        return PaymentCalculationResult.builder()
                .totalAmount(totalAmount)
                .driverAmount(driverAmount)
                .platformFee(platformFee)
                .build();
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .deliveryId(payment.getDelivery().getId())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .amount(payment.getAmount())
                .driverAmount(payment.getDriverAmount())
                .platformFee(payment.getPlatformFee())
                .transactionId(payment.getTransactionId())
                .pixKey(payment.getPixKey())
                .cardLastFourDigits(payment.getCardLastFourDigits())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
