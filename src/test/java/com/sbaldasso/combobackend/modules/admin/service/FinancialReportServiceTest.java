package com.sbaldasso.combobackend.modules.admin.service;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.delivery.repository.DeliveryRepository;
import com.sbaldasso.combobackend.modules.admin.dto.FinancialReportSummary;
import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import com.sbaldasso.combobackend.modules.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FinancialReportServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private FinancialReportService financialReportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateDailyReport_returnsCorrectSummary() {
        // Arrange
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999);

        when(deliveryRepository.countByCreatedAtBetween(startOfDay, endOfDay)).thenReturn(100L);
        when(deliveryRepository.countByStatusAndCreatedAtBetween(
                eq(DeliveryStatus.FINISHED), any(), any())).thenReturn(90L);
        when(deliveryRepository.countByStatusAndCreatedAtBetween(
                eq(DeliveryStatus.CANCELLED), any(), any())).thenReturn(10L);

        when(paymentRepository.sumAmountByDateAndStatus(
                any(), any(), eq(Payment.PaymentStatus.COMPLETED)))
                .thenReturn(BigDecimal.valueOf(5000.00));
        when(paymentRepository.sumPlatformFeesByDateAndStatus(
                any(), any(), eq(Payment.PaymentStatus.COMPLETED)))
                .thenReturn(BigDecimal.valueOf(500.00));
        when(paymentRepository.sumDriverAmountByDateAndStatus(
                any(), any(), eq(Payment.PaymentStatus.COMPLETED)))
                .thenReturn(BigDecimal.valueOf(4000.00));
        when(paymentRepository.sumCancellationFeesByDate(any(), any()))
                .thenReturn(BigDecimal.valueOf(100.00));

        // Act
        FinancialReportSummary summary = financialReportService.generateDailyReport(date);

        // Assert
        assertNotNull(summary);
        assertEquals(date, summary.getDate());
        assertEquals(100L, summary.getTotalDeliveries());
        assertEquals(90L, summary.getCompletedDeliveries());
        assertEquals(10L, summary.getCancelledDeliveries());
        assertEquals(BigDecimal.valueOf(5000.00), summary.getTotalRevenue());
        assertEquals(BigDecimal.valueOf(500.00), summary.getPlatformFees());
        assertEquals(BigDecimal.valueOf(4000.00), summary.getDriversPayouts());
        assertEquals(BigDecimal.valueOf(100.00), summary.getCancellationFees());
        assertEquals(90.0, summary.getCompletionRate());
        assertEquals(BigDecimal.valueOf(55.56), summary.getAverageDeliveryValue().setScale(2));
    }

    @Test
    void generateDailyReport_withNoDeliveries_returnsZeroValues() {
        // Arrange
        LocalDate date = LocalDate.now();

        when(deliveryRepository.countByCreatedAtBetween(any(), any())).thenReturn(0L);
        when(deliveryRepository.countByStatusAndCreatedAtBetween(any(), any(), any())).thenReturn(0L);
        when(paymentRepository.sumAmountByDateAndStatus(any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.sumPlatformFeesByDateAndStatus(any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.sumDriverAmountByDateAndStatus(any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.sumCancellationFeesByDate(any(), any())).thenReturn(BigDecimal.ZERO);

        // Act
        FinancialReportSummary summary = financialReportService.generateDailyReport(date);

        // Assert
        assertNotNull(summary);
        assertEquals(date, summary.getDate());
        assertEquals(0L, summary.getTotalDeliveries());
        assertEquals(0L, summary.getCompletedDeliveries());
        assertEquals(0L, summary.getCancelledDeliveries());
        assertEquals(BigDecimal.ZERO, summary.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, summary.getPlatformFees());
        assertEquals(BigDecimal.ZERO, summary.getDriversPayouts());
        assertEquals(BigDecimal.ZERO, summary.getCancellationFees());
        assertEquals(0.0, summary.getCompletionRate());
        assertEquals(BigDecimal.ZERO, summary.getAverageDeliveryValue());
    }

    @Test
    void generateWeeklyReport_aggregatesDataCorrectly() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        when(deliveryRepository.countByCreatedAtBetween(any(), any())).thenReturn(700L);
        when(deliveryRepository.countByStatusAndCreatedAtBetween(
                eq(DeliveryStatus.FINISHED), any(), any())).thenReturn(630L);
        when(deliveryRepository.countByStatusAndCreatedAtBetween(
                eq(DeliveryStatus.CANCELLED), any(), any())).thenReturn(70L);

        when(paymentRepository.sumAmountByDateAndStatus(
                any(), any(), eq(Payment.PaymentStatus.COMPLETED)))
                .thenReturn(BigDecimal.valueOf(35000.00));
        when(paymentRepository.sumPlatformFeesByDateAndStatus(
                any(), any(), eq(Payment.PaymentStatus.COMPLETED)))
                .thenReturn(BigDecimal.valueOf(3500.00));
        when(paymentRepository.sumDriverAmountByDateAndStatus(
                any(), any(), eq(Payment.PaymentStatus.COMPLETED)))
                .thenReturn(BigDecimal.valueOf(28000.00));
        when(paymentRepository.sumCancellationFeesByDate(any(), any()))
                .thenReturn(BigDecimal.valueOf(700.00));

        // Act
        FinancialReportSummary summary = financialReportService.generateWeeklyReport(startDate, endDate);

        // Assert
        assertNotNull(summary);
        assertEquals(700L, summary.getTotalDeliveries());
        assertEquals(630L, summary.getCompletedDeliveries());
        assertEquals(70L, summary.getCancelledDeliveries());
        assertEquals(BigDecimal.valueOf(35000.00), summary.getTotalRevenue());
        assertEquals(BigDecimal.valueOf(3500.00), summary.getPlatformFees());
        assertEquals(BigDecimal.valueOf(28000.00), summary.getDriversPayouts());
        assertEquals(BigDecimal.valueOf(700.00), summary.getCancellationFees());
        assertEquals(90.0, summary.getCompletionRate());
    }

    @Test
    void generateMonthlyReport_aggregatesDataCorrectly() {
        // Arrange
        int year = 2025;
        int month = 9;

        when(deliveryRepository.countByCreatedAtBetween(any(), any())).thenReturn(3000L);
        when(deliveryRepository.countByStatusAndCreatedAtBetween(
                eq(DeliveryStatus.FINISHED), any(), any())).thenReturn(2700L);
        when(deliveryRepository.countByStatusAndCreatedAtBetween(
                eq(DeliveryStatus.CANCELLED), any(), any())).thenReturn(300L);

        when(paymentRepository.sumAmountByDateAndStatus(
                any(), any(), eq(Payment.PaymentStatus.COMPLETED)))
                .thenReturn(BigDecimal.valueOf(150000.00));
        when(paymentRepository.sumPlatformFeesByDateAndStatus(
                any(), any(), eq(Payment.PaymentStatus.COMPLETED)))
                .thenReturn(BigDecimal.valueOf(15000.00));
        when(paymentRepository.sumDriverAmountByDateAndStatus(
                any(), any(), eq(Payment.PaymentStatus.COMPLETED)))
                .thenReturn(BigDecimal.valueOf(120000.00));
        when(paymentRepository.sumCancellationFeesByDate(any(), any()))
                .thenReturn(BigDecimal.valueOf(3000.00));

        // Act
        FinancialReportSummary summary = financialReportService.generateMonthlyReport(year, month);

        // Assert
        assertNotNull(summary);
        assertEquals(3000L, summary.getTotalDeliveries());
        assertEquals(2700L, summary.getCompletedDeliveries());
        assertEquals(300L, summary.getCancelledDeliveries());
        assertEquals(BigDecimal.valueOf(150000.00), summary.getTotalRevenue());
        assertEquals(BigDecimal.valueOf(15000.00), summary.getPlatformFees());
        assertEquals(BigDecimal.valueOf(120000.00), summary.getDriversPayouts());
        assertEquals(BigDecimal.valueOf(3000.00), summary.getCancellationFees());
        assertEquals(90.0, summary.getCompletionRate());
    }
}
