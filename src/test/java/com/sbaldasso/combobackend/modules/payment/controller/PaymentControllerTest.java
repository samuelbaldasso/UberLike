package com.sbaldasso.combobackend.modules.payment.controller;

import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import com.sbaldasso.combobackend.modules.payment.dto.CreatePaymentRequest;
import com.sbaldasso.combobackend.modules.payment.dto.PaymentResponse;
import com.sbaldasso.combobackend.modules.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {
    @Mock
    private PaymentService paymentService;
    
    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPayment_returnsPaymentResponse() {
        // Arrange
        UUID deliveryId = UUID.randomUUID();
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .amount(BigDecimal.valueOf(50.0))
                .paymentMethod("PIX")
                .build();
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .id(1L)
                .amount(request.getAmount())
                .status(Payment.PaymentStatus.PENDING)
                .build();
        
        when(paymentService.createPayment(deliveryId, request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.createPayment(deliveryId, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(paymentService).createPayment(deliveryId, request);
    }

    @Test
    void getPayments_returnsPageOfPayments() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Page<PaymentResponse> expectedPage = new PageImpl<>(Collections.emptyList());
        when(paymentService.getPaymentsForUser(userId, Pageable.unpaged())).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<PaymentResponse>> response = paymentController.getPayments(userId, Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(paymentService).getPaymentsForUser(userId, Pageable.unpaged());
    }

    @Test
    void processPayment_returnsUpdatedPayment() {
        // Arrange
        Long paymentId = 1L;
        String pixCode = "123456789";
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .id(paymentId)
                .status(Payment.PaymentStatus.COMPLETED)
                .build();
        
        when(paymentService.processPayment(paymentId, pixCode)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.processPayment(paymentId, pixCode);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(paymentService).processPayment(paymentId, pixCode);
    }

    @Test
    void refundPayment_returnsUpdatedPayment() {
        // Arrange
        Long paymentId = 1L;
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .id(paymentId)
                .status(Payment.PaymentStatus.REFUNDED)
                .build();
        
        when(paymentService.refundPayment(paymentId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.refundPayment(paymentId);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(paymentService).refundPayment(paymentId);
    }

    @Test
    void getPayment_returnsPayment() {
        // Arrange
        Long paymentId = 1L;
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .id(paymentId)
                .status(Payment.PaymentStatus.COMPLETED)
                .build();
        
        when(paymentService.getPaymentById(paymentId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.getPayment(paymentId);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(paymentService).getPaymentById(paymentId);
    }
}
