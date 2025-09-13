package com.sbaldasso.combobackend.modules.payment.service;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.service.DeliveryService;
import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import com.sbaldasso.combobackend.modules.payment.dto.CreatePaymentRequest;
import com.sbaldasso.combobackend.modules.payment.dto.PaymentResponse;
import com.sbaldasso.combobackend.modules.payment.repository.PaymentRepository;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.domain.UserType;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserService userService;
    @Mock
    private DeliveryService deliveryService;
    
    @InjectMocks
    private PaymentService paymentService;

    private User customer;
    private User driver;
    private Delivery delivery;
    private Payment payment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new User();
        customer.setId(UUID.randomUUID());
        customer.setUserType(UserType.CUSTOMER);

        driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUserType(UserType.DRIVER);

        delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setCustomer(customer);
        delivery.setDriver(driver);

        payment = Payment.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50.0))
                .platformFee(BigDecimal.valueOf(5.0))
                .driverAmount(BigDecimal.valueOf(45.0))
                .status(Payment.PaymentStatus.PENDING)
                .build();
    }

    @Test
    void createPayment_savesAndReturnsResponse() {
        // Arrange
        UUID deliveryId = UUID.randomUUID();
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .amount(BigDecimal.valueOf(50.0))
                .paymentMethod("PIX")
                .build();

        when(deliveryService.getDeliveryById(deliveryId)).thenReturn(delivery);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentResponse response = paymentService.createPayment(deliveryId, request);

        // Assert
        assertNotNull(response);
        assertEquals(payment.getId(), response.getId());
        assertEquals(payment.getAmount(), response.getAmount());
        assertEquals(payment.getStatus(), response.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void getPaymentsForUser_returnsPageOfPayments() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Page<Payment> paymentsPage = new PageImpl<>(Collections.singletonList(payment));
        
        when(userService.validateAndGetUser(userId)).thenReturn(customer);
        when(paymentRepository.findByCustomerId(userId, Pageable.unpaged())).thenReturn(paymentsPage);

        // Act
        Page<PaymentResponse> response = paymentService.getPaymentsForUser(userId, Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(payment.getId(), response.getContent().get(0).getId());
    }

    @Test
    void processPayment_updatesStatusAndReturnsResponse() {
        // Arrange
        Long paymentId = 1L;
        String pixCode = "123456789";
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentResponse response = paymentService.processPayment(paymentId, pixCode);

        // Assert
        assertNotNull(response);
        assertEquals(Payment.PaymentStatus.COMPLETED, response.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void processPayment_throwsException_whenPaymentNotFound() {
        // Arrange
        Long paymentId = 1L;
        String pixCode = "123456789";

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(paymentId, pixCode));
    }

    @Test
    void refundPayment_updatesStatusAndReturnsResponse() {
        // Arrange
        Long paymentId = 1L;
        payment.setStatus(Payment.PaymentStatus.REFUNDED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentResponse response = paymentService.refundPayment(paymentId);

        // Assert
        assertNotNull(response);
        assertEquals(Payment.PaymentStatus.REFUNDED, response.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void refundPayment_throwsException_whenPaymentNotFound() {
        // Arrange
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> paymentService.refundPayment(paymentId));
    }

    @Test
    void getPaymentById_returnsPaymentResponse() {
        // Arrange
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act
        PaymentResponse response = paymentService.getPaymentById(paymentId);

        // Assert
        assertNotNull(response);
        assertEquals(payment.getId(), response.getId());
        assertEquals(payment.getAmount(), response.getAmount());
        assertEquals(payment.getStatus(), response.getStatus());
    }

    @Test
    void getPaymentById_throwsException_whenPaymentNotFound() {
        // Arrange
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> paymentService.getPaymentById(paymentId));
    }
}
