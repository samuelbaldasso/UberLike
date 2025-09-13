package com.sbaldasso.combobackend.modules.admin.service;

import com.sbaldasso.combobackend.modules.admin.dto.DriverPerformanceResponse;
import com.sbaldasso.combobackend.modules.admin.dto.PerformanceMetricsResponse;
import com.sbaldasso.combobackend.modules.admin.dto.RegionPerformanceResponse;
import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.delivery.repository.DeliveryRepository;
import com.sbaldasso.combobackend.modules.payment.repository.PaymentRepository;
import com.sbaldasso.combobackend.modules.rating.repository.RatingRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PerformanceReportServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PerformanceReportService performanceReportService;

    private User driver;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUserType(UserType.DRIVER);

        startDate = LocalDate.now().minusDays(30);
        endDate = LocalDate.now();
    }

    @Test
    void getDriverPerformance_returnsPerformanceMetrics() {
        // Arrange
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);
        when(deliveryRepository.countByDriverIdAndCreatedAtBetween(any(), any(), any()))
                .thenReturn(100L);
        when(deliveryRepository.countByDriverIdAndStatusAndCreatedAtBetween(
                any(), eq(DeliveryStatus.FINISHED), any(), any())).thenReturn(95L);
        when(deliveryRepository.countByDriverIdAndStatusAndCreatedAtBetween(
                any(), eq(DeliveryStatus.CANCELLED), any(), any())).thenReturn(5L);
        when(ratingRepository.calculateAverageRatingForUserBetweenDates(
                any(), any(), any())).thenReturn(4.8);
        when(paymentRepository.sumDriverEarningsBetweenDates(
                any(), any(), any())).thenReturn(BigDecimal.valueOf(5000.00));
        when(deliveryRepository.calculateAverageResponseTime(
                any(), any(), any())).thenReturn(28.5);

        // Act
        DriverPerformanceResponse response = performanceReportService.getDriverPerformance(
            driver.getId(), startDate, endDate);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getTotalDeliveries());
        assertEquals(95L, response.getCompletedDeliveries());
        assertEquals(5L, response.getCancelledDeliveries());
        assertEquals(4.8, response.getAverageRating());
        assertEquals(BigDecimal.valueOf(5000.00), response.getTotalEarnings());
        assertEquals(95.0, response.getCompletionRate());
        assertEquals(28.5, response.getAverageResponseTime());
    }

    @Test
    void getTopPerformingDrivers_returnsPageOfDrivers() {
        // Arrange
        List<UUID> topDriverIds = Collections.singletonList(driver.getId());
        when(deliveryRepository.findTopDriversByDeliveryCount(any(), any(), any()))
                .thenReturn(topDriverIds);
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);

        // Mock all necessary metrics for each driver
        when(deliveryRepository.countByDriverIdAndCreatedAtBetween(any(), any(), any()))
                .thenReturn(100L);
        when(deliveryRepository.countByDriverIdAndStatusAndCreatedAtBetween(
                any(), any(), any(), any())).thenReturn(95L);
        when(ratingRepository.calculateAverageRatingForUserBetweenDates(
                any(), any(), any())).thenReturn(4.8);
        when(paymentRepository.sumDriverEarningsBetweenDates(
                any(), any(), any())).thenReturn(BigDecimal.valueOf(5000.00));

        // Act
        Page<DriverPerformanceResponse> response = performanceReportService
            .getTopPerformingDrivers(startDate, endDate, Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(driver.getId(), response.getContent().get(0).getDriverId());
    }

    @Test
    void getRegionPerformance_returnsRegionMetrics() {
        // Arrange
        String region = "DOWNTOWN";
        when(deliveryRepository.countByRegionAndCreatedAtBetween(
                eq(region), any(), any())).thenReturn(500L);
        when(paymentRepository.sumRevenueByRegionBetweenDates(
                eq(region), any(), any())).thenReturn(BigDecimal.valueOf(25000.00));
        when(deliveryRepository.calculateAverageDeliveryTimeByRegion(
                eq(region), any(), any())).thenReturn(25.0);
        when(deliveryRepository.countActiveDriversByRegion(
                eq(region), any(), any())).thenReturn(20L);
        when(deliveryRepository.findPeakHoursByRegion(
                eq(region), any(), any())).thenReturn(List.of("12:00", "13:00", "18:00"));

        // Act
        RegionPerformanceResponse response = performanceReportService
            .getRegionPerformance(region, startDate, endDate);

        // Assert
        assertNotNull(response);
        assertEquals(region, response.getRegion());
        assertEquals(500L, response.getTotalDeliveries());
        assertEquals(BigDecimal.valueOf(25000.00), response.getTotalRevenue());
        assertEquals(25.0, response.getAverageDeliveryTime());
        assertEquals(20L, response.getActiveDrivers());
        assertEquals(3, response.getPeakHours().size());
    }

    @Test
    void getOverallMetrics_returnsSystemMetrics() {
        // Arrange
        when(deliveryRepository.countByCreatedAtBetween(any(), any()))
                .thenReturn(2000L);
        when(paymentRepository.sumTotalRevenueBetweenDates(any(), any()))
                .thenReturn(BigDecimal.valueOf(100000.00));
        when(deliveryRepository.calculateAverageDeliveryTime(any(), any()))
                .thenReturn(28.5);
        when(deliveryRepository.countActiveDrivers(any(), any()))
                .thenReturn(100L);
        when(deliveryRepository.calculateCompletionRate(any(), any()))
                .thenReturn(95.0);
        when(ratingRepository.calculateAverageRatingBetweenDates(any(), any()))
                .thenReturn(4.7);

        // Act
        PerformanceMetricsResponse response = performanceReportService
            .getOverallMetrics(startDate, endDate);

        // Assert
        assertNotNull(response);
        assertEquals(2000L, response.getTotalDeliveries());
        assertEquals(BigDecimal.valueOf(100000.00), response.getTotalRevenue());
        assertEquals(28.5, response.getAverageDeliveryTime());
        assertEquals(100L, response.getActiveDrivers());
        assertEquals(95.0, response.getCompletionRate());
        assertEquals(4.7, response.getAverageRating());
    }

    @Test
    void getLowPerformingDrivers_returnsPageOfDrivers() {
        // Arrange
        List<UUID> lowPerformingDriverIds = Collections.singletonList(driver.getId());
        when(deliveryRepository.findLowPerformingDrivers(any(), any(), any()))
                .thenReturn(lowPerformingDriverIds);
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);

        // Mock metrics for low performing drivers
        when(deliveryRepository.countByDriverIdAndCreatedAtBetween(any(), any(), any()))
                .thenReturn(20L);
        when(deliveryRepository.countByDriverIdAndStatusAndCreatedAtBetween(
                any(), any(), any(), any())).thenReturn(15L);
        when(ratingRepository.calculateAverageRatingForUserBetweenDates(
                any(), any(), any())).thenReturn(3.5);
        when(paymentRepository.sumDriverEarningsBetweenDates(
                any(), any(), any())).thenReturn(BigDecimal.valueOf(1000.00));

        // Act
        Page<DriverPerformanceResponse> response = performanceReportService
            .getLowPerformingDrivers(startDate, endDate, Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        DriverPerformanceResponse driverMetrics = response.getContent().get(0);
        assertEquals(driver.getId(), driverMetrics.getDriverId());
        assertEquals(3.5, driverMetrics.getAverageRating());
        assertTrue(driverMetrics.getCompletionRate() < 90.0);
    }

    @Test
    void getDriverPerformance_throwsException_whenUserNotDriver() {
        // Arrange
        driver.setUserType(UserType.CUSTOMER);
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            performanceReportService.getDriverPerformance(driver.getId(), startDate, endDate));
    }

    @Test
    void getDriverPerformance_throwsException_whenInvalidDateRange() {
        // Arrange
        LocalDate invalidStartDate = LocalDate.now();
        LocalDate invalidEndDate = LocalDate.now().minusDays(30);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            performanceReportService.getDriverPerformance(
                driver.getId(), invalidStartDate, invalidEndDate));
    }
}
