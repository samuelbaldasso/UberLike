package com.sbaldasso.combobackend.modules.admin.controller;

import com.sbaldasso.combobackend.modules.admin.dto.DriverPerformanceResponse;
import com.sbaldasso.combobackend.modules.admin.dto.PerformanceMetricsResponse;
import com.sbaldasso.combobackend.modules.admin.dto.RegionPerformanceResponse;
import com.sbaldasso.combobackend.modules.admin.service.PerformanceReportService;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerformanceReportControllerTest {

    @Mock
    private PerformanceReportService performanceReportService;

    @InjectMocks
    private PerformanceReportController performanceReportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDriverPerformance_returnsDriverPerformanceResponse() {
        // Arrange
        UUID driverId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        DriverPerformanceResponse expectedResponse = DriverPerformanceResponse.builder()
                .driverId(driverId)
                .totalDeliveries(100L)
                .completedDeliveries(95L)
                .cancelledDeliveries(5L)
                .averageRating(4.8)
                .totalEarnings(BigDecimal.valueOf(5000.00))
                .completionRate(95.0)
                .averageResponseTime(28.5)
                .build();

        when(performanceReportService.getDriverPerformance(driverId, startDate, endDate))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DriverPerformanceResponse> response = 
            performanceReportController.getDriverPerformance(driverId, startDate, endDate);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(performanceReportService).getDriverPerformance(driverId, startDate, endDate);
    }

    @Test
    void getTopPerformingDrivers_returnsPageOfDrivers() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        Page<DriverPerformanceResponse> expectedPage = new PageImpl<>(Collections.emptyList());
        when(performanceReportService.getTopPerformingDrivers(startDate, endDate, Pageable.unpaged()))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DriverPerformanceResponse>> response = 
            performanceReportController.getTopPerformingDrivers(startDate, endDate, Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(performanceReportService).getTopPerformingDrivers(startDate, endDate, Pageable.unpaged());
    }

    @Test
    void getRegionPerformance_returnsRegionPerformanceResponse() {
        // Arrange
        String region = "DOWNTOWN";
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        RegionPerformanceResponse expectedResponse = RegionPerformanceResponse.builder()
                .region(region)
                .totalDeliveries(500L)
                .totalRevenue(BigDecimal.valueOf(25000.00))
                .averageDeliveryTime(25.0)
                .activeDrivers(20L)
                .peakHours(List.of("12:00", "13:00", "18:00"))
                .build();

        when(performanceReportService.getRegionPerformance(region, startDate, endDate))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<RegionPerformanceResponse> response = 
            performanceReportController.getRegionPerformance(region, startDate, endDate);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(performanceReportService).getRegionPerformance(region, startDate, endDate);
    }

    @Test
    void getOverallMetrics_returnsPerformanceMetricsResponse() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        PerformanceMetricsResponse expectedResponse = PerformanceMetricsResponse.builder()
                .totalDeliveries(2000L)
                .totalRevenue(BigDecimal.valueOf(100000.00))
                .averageDeliveryTime(28.5)
                .activeDrivers(100L)
                .completionRate(95.0)
                .averageRating(4.7)
                .build();

        when(performanceReportService.getOverallMetrics(startDate, endDate))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<PerformanceMetricsResponse> response = 
            performanceReportController.getOverallMetrics(startDate, endDate);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(performanceReportService).getOverallMetrics(startDate, endDate);
    }

    @Test
    void getLowPerformingDrivers_returnsPageOfDrivers() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        Page<DriverPerformanceResponse> expectedPage = new PageImpl<>(Collections.emptyList());
        when(performanceReportService.getLowPerformingDrivers(startDate, endDate, Pageable.unpaged()))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DriverPerformanceResponse>> response = 
            performanceReportController.getLowPerformingDrivers(startDate, endDate, Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(performanceReportService).getLowPerformingDrivers(startDate, endDate, Pageable.unpaged());
    }
}
