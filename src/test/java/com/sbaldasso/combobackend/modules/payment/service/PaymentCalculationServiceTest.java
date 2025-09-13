package com.sbaldasso.combobackend.modules.payment.service;

import com.sbaldasso.combobackend.modules.payment.config.PaymentConfig;
import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import com.sbaldasso.combobackend.modules.payment.dto.PaymentCalculationResult;
import com.sbaldasso.combobackend.modules.payment.dto.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCalculationServiceTest {

    @Mock
    private PaymentConfig paymentConfig;

    @InjectMocks
    private PaymentCalculationService calculationService;

    @BeforeEach
    void setUp() {
        when(paymentConfig.getMinimumFee()).thenReturn(BigDecimal.valueOf(10.0));
        when(paymentConfig.getPricePerKm()).thenReturn(BigDecimal.valueOf(2.0));
        when(paymentConfig.getPricePerMinute()).thenReturn(BigDecimal.valueOf(0.5));
        when(paymentConfig.getPlatformFeePercentage()).thenReturn(BigDecimal.valueOf(10.0));
    }

    @Test
    void calculatePayment_ShouldCalculateCorrectly() {
        // Arrange
        double distance = 5.0; // 5km
        int time = 20; // 20 minutos

        // Act
        PaymentCalculationResult result = calculationService.calculatePayment(distance, time);

        // Assert
        // Valor pela distância: 5km * R$2 = R$10
        // Valor pelo tempo: 20min * R$0.5 = R$10
        // Total: R$20
        // Taxa da plataforma (10%): R$2
        // Valor do motorista: R$18
        assertEquals(0, BigDecimal.valueOf(20.0).compareTo(result.getTotalAmount()));
        assertEquals(0, BigDecimal.valueOf(2.0).compareTo(result.getPlatformFee()));
        assertEquals(0, BigDecimal.valueOf(18.0).compareTo(result.getDriverAmount()));
    }

    @Test
    void calculatePayment_WhenBelowMinimum_ShouldUseMinimumFee() {
        // Arrange
        double distance = 1.0; // 1km
        int time = 5; // 5 minutos

        // Act
        PaymentCalculationResult result = calculationService.calculatePayment(distance, time);

        // Assert
        // Valor calculado seria: (1km * R$2) + (5min * R$0.5) = R$4.5
        // Mas como é menor que o mínimo (R$10), usa o mínimo
        assertEquals(0, BigDecimal.valueOf(10.0).compareTo(result.getTotalAmount()));
        assertEquals(0, BigDecimal.valueOf(1.0).compareTo(result.getPlatformFee()));
        assertEquals(0, BigDecimal.valueOf(9.0).compareTo(result.getDriverAmount()));
    }

    @Test
    void toPaymentResponse_ShouldMapCorrectly() {
        // Arrange
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.0));
        payment.setDriverAmount(BigDecimal.valueOf(90.0));
        payment.setPlatformFee(BigDecimal.valueOf(10.0));
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setMethod(Payment.PaymentMethod.PIX);
        payment.setTransactionId("tx123");
        payment.setPixKey("pix123");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        // Act
        PaymentResponse response = calculationService.toPaymentResponse(payment);

        // Assert
        assertEquals(payment.getId(), response.getId());
        assertEquals(payment.getAmount(), response.getAmount());
        assertEquals(payment.getDriverAmount(), response.getDriverAmount());
        assertEquals(payment.getPlatformFee(), response.getPlatformFee());
        assertEquals(payment.getStatus(), response.getStatus());
        assertEquals(payment.getMethod(), response.getMethod());
        assertEquals(payment.getTransactionId(), response.getTransactionId());
        assertEquals(payment.getPixKey(), response.getPixKey());
        assertEquals(payment.getCreatedAt(), response.getCreatedAt());
        assertEquals(payment.getUpdatedAt(), response.getUpdatedAt());
    }
}
