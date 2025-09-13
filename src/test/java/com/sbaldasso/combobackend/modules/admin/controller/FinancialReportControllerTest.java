package com.sbaldasso.combobackend.modules.admin.controller;

import com.sbaldasso.combobackend.modules.admin.dto.FinancialReportSummary;
import com.sbaldasso.combobackend.modules.admin.service.FinancialReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialReportControllerTest {
    
    @Mock
    private FinancialReportService financialReportService;

    @InjectMocks
    private FinancialReportController financialReportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDailyReport_returnsFinancialReportSummary() {
        // Arrange
        LocalDate date = LocalDate.now();
        FinancialReportSummary expectedSummary = FinancialReportSummary.builder()
                .date(date)
                .totalDeliveries(100L)
                .completedDeliveries(90L)
                .cancelledDeliveries(10L)
                .totalRevenue(BigDecimal.valueOf(5000.00))
                .platformFees(BigDecimal.valueOf(500.00))
                .driversPayouts(BigDecimal.valueOf(4000.00))
                .cancellationFees(BigDecimal.valueOf(100.00))
                .completionRate(90.0)
                .averageDeliveryValue(BigDecimal.valueOf(50.00))
                .build();

        when(financialReportService.generateDailyReport(date)).thenReturn(expectedSummary);

        // Act
        ResponseEntity<FinancialReportSummary> response = financialReportController.getDailyReport(date);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedSummary, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(financialReportService).generateDailyReport(date);
    }

    @Test
    void getWeeklyReport_returnsFinancialReportSummary() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        FinancialReportSummary expectedSummary = FinancialReportSummary.builder()
                .date(endDate)
                .totalDeliveries(700L)
                .completedDeliveries(630L)
                .cancelledDeliveries(70L)
                .totalRevenue(BigDecimal.valueOf(35000.00))
                .platformFees(BigDecimal.valueOf(3500.00))
                .driversPayouts(BigDecimal.valueOf(28000.00))
                .cancellationFees(BigDecimal.valueOf(700.00))
                .completionRate(90.0)
                .averageDeliveryValue(BigDecimal.valueOf(50.00))
                .build();

        when(financialReportService.generateWeeklyReport(startDate, endDate)).thenReturn(expectedSummary);

        // Act
        ResponseEntity<FinancialReportSummary> response = financialReportController.getWeeklyReport(startDate, endDate);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedSummary, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(financialReportService).generateWeeklyReport(startDate, endDate);
    }

    @Test
    void getMonthlyReport_returnsFinancialReportSummary() {
        // Arrange
        int year = 2025;
        int month = 9;
        FinancialReportSummary expectedSummary = FinancialReportSummary.builder()
                .date(LocalDate.of(year, month, 1))
                .totalDeliveries(3000L)
                .completedDeliveries(2700L)
                .cancelledDeliveries(300L)
                .totalRevenue(BigDecimal.valueOf(150000.00))
                .platformFees(BigDecimal.valueOf(15000.00))
                .driversPayouts(BigDecimal.valueOf(120000.00))
                .cancellationFees(BigDecimal.valueOf(3000.00))
                .completionRate(90.0)
                .averageDeliveryValue(BigDecimal.valueOf(50.00))
                .build();

        when(financialReportService.generateMonthlyReport(year, month)).thenReturn(expectedSummary);

        // Act
        ResponseEntity<FinancialReportSummary> response = financialReportController.getMonthlyReport(year, month);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedSummary, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(financialReportService).generateMonthlyReport(year, month);
    }
}
