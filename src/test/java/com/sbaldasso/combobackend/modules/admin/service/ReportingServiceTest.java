package com.sbaldasso.combobackend.modules.admin.service;

import com.sbaldasso.combobackend.modules.admin.dto.*;
import com.sbaldasso.combobackend.modules.delivery.repository.DeliveryRepository;
import com.sbaldasso.combobackend.modules.payment.repository.PaymentRepository;
import com.sbaldasso.combobackend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportingServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportingService reportingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getWeeklyReport_shouldReturnCompleteReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        when(deliveryRepository.countDeliveriesByDateRange(any(), any()))
            .thenReturn(150L);
        when(paymentRepository.calculateRevenueByDateRange(any(), any()))
            .thenReturn(BigDecimal.valueOf(5000.00));
        when(deliveryRepository.calculateAverageDeliveryTimeByDateRange(any(), any()))
            .thenReturn(25.0);
        when(userRepository.countNewDriversByDateRange(any(), any()))
            .thenReturn(10L);

        // Act
        WeeklyReportResponse report = reportingService.generateWeeklyReport(startDate);

        // Assert
        assertNotNull(report);
        assertEquals(150L, report.getTotalDeliveries());
        assertEquals(BigDecimal.valueOf(5000.00), report.getTotalRevenue());
        assertEquals(25.0, report.getAverageDeliveryTime());
        assertEquals(10L, report.getNewDrivers());
        assertTrue(report.getStartDate().isEqual(startDate));
        assertTrue(report.getEndDate().isEqual(endDate));
    }

    @Test
    void getMonthlyReport_shouldReturnCompleteReport() {
        // Arrange
        YearMonth yearMonth = YearMonth.now();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        when(deliveryRepository.countDeliveriesByDateRange(any(), any()))
            .thenReturn(600L);
        when(paymentRepository.calculateRevenueByDateRange(any(), any()))
            .thenReturn(BigDecimal.valueOf(20000.00));
        when(userRepository.countActiveDriversByMonth(any()))
            .thenReturn(50L);
        when(paymentRepository.calculateAverageDriverEarningsByMonth(any()))
            .thenReturn(BigDecimal.valueOf(2000.00));

        // Act
        MonthlyReportResponse report = reportingService.generateMonthlyReport(yearMonth);

        // Assert
        assertNotNull(report);
        assertEquals(600L, report.getTotalDeliveries());
        assertEquals(BigDecimal.valueOf(20000.00), report.getTotalRevenue());
        assertEquals(50L, report.getActiveDrivers());
        assertEquals(BigDecimal.valueOf(2000.00), report.getAverageDriverEarnings());
        assertEquals(yearMonth, report.getYearMonth());
    }

    @Test
    void getRevenueTrends_shouldReturnTrendAnalysis() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now();
        
        List<RevenueTrendData> mockTrendData = List.of(
            new RevenueTrendData(YearMonth.now().minusMonths(2), BigDecimal.valueOf(15000.00)),
            new RevenueTrendData(YearMonth.now().minusMonths(1), BigDecimal.valueOf(18000.00)),
            new RevenueTrendData(YearMonth.now(), BigDecimal.valueOf(20000.00))
        );

        when(paymentRepository.getRevenueTrendsByDateRange(startDate, endDate))
            .thenReturn(mockTrendData);

        // Act
        RevenueTrendResponse trends = reportingService.getRevenueTrends(startDate, endDate);

        // Assert
        assertNotNull(trends);
        assertEquals(3, trends.getTrendData().size());
        assertEquals(BigDecimal.valueOf(20000.00), trends.getTrendData().get(2).getRevenue());
        assertTrue(trends.getGrowthRate().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void getDriverEarningsReport_shouldReturnEarningsData() {
        // Arrange
        Long driverId = 1L;
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        when(paymentRepository.calculateDriverEarningsByDateRange(driverId, startDate, endDate))
            .thenReturn(BigDecimal.valueOf(3000.00));
        when(deliveryRepository.countDriverDeliveriesByDateRange(driverId, startDate, endDate))
            .thenReturn(120L);
        when(paymentRepository.calculateDriverAverageTripEarnings(driverId, startDate, endDate))
            .thenReturn(BigDecimal.valueOf(25.00));

        // Act
        DriverEarningsResponse report = reportingService.generateDriverEarningsReport(driverId, startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(BigDecimal.valueOf(3000.00), report.getTotalEarnings());
        assertEquals(120L, report.getTotalDeliveries());
        assertEquals(BigDecimal.valueOf(25.00), report.getAverageEarningsPerTrip());
        assertEquals(driverId, report.getDriverId());
    }

    @Test
    void getCustomDateRangeReport_shouldReturnCustomReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(2);
        LocalDate endDate = LocalDate.now();

        when(deliveryRepository.countDeliveriesByDateRange(startDate, endDate))
            .thenReturn(1000L);
        when(paymentRepository.calculateRevenueByDateRange(startDate, endDate))
            .thenReturn(BigDecimal.valueOf(30000.00));
        when(deliveryRepository.calculateAverageDeliveryTimeByDateRange(startDate, endDate))
            .thenReturn(22.5);
        when(userRepository.countActiveDriversByDateRange(startDate, endDate))
            .thenReturn(75L);

        // Act
        CustomDateRangeReportResponse report = reportingService.generateCustomDateRangeReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(1000L, report.getTotalDeliveries());
        assertEquals(BigDecimal.valueOf(30000.00), report.getTotalRevenue());
        assertEquals(22.5, report.getAverageDeliveryTime());
        assertEquals(75L, report.getActiveDrivers());
        assertTrue(report.getStartDate().isEqual(startDate));
        assertTrue(report.getEndDate().isEqual(endDate));
    }

    @Test
    void getReports_shouldHandleEmptyData() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        when(deliveryRepository.countDeliveriesByDateRange(any(), any()))
            .thenReturn(0L);
        when(paymentRepository.calculateRevenueByDateRange(any(), any()))
            .thenReturn(BigDecimal.ZERO);

        // Act
        CustomDateRangeReportResponse report = reportingService.generateCustomDateRangeReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(0L, report.getTotalDeliveries());
        assertEquals(BigDecimal.ZERO, report.getTotalRevenue());
        assertEquals(0.0, report.getAverageDeliveryTime());
        assertEquals(0L, report.getActiveDrivers());
    }

    @Test
    void invalidDateRange_shouldThrowException() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            reportingService.generateCustomDateRangeReport(futureDate, LocalDate.now()));
    }
}
