package com.sbaldasso.combobackend.modules.admin.controller;

import com.sbaldasso.combobackend.modules.admin.dto.DashboardMetricsResponse;
import com.sbaldasso.combobackend.modules.admin.dto.DashboardSummaryResponse;
import com.sbaldasso.combobackend.modules.admin.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDashboardSummary_returnsDashboardSummary() {
        // Arrange
        DashboardSummaryResponse expectedResponse = DashboardSummaryResponse.builder()
                .activeDeliveries(50L)
                .availableDrivers(100L)
                .pendingApprovals(10L)
                .openDisputes(5L)
                .dailyRevenue(BigDecimal.valueOf(5000.00))
                .dailyDeliveries(200L)
                .averageDeliveryTime(25.0)
                .systemHealth("HEALTHY")
                .lastUpdated(LocalDateTime.now())
                .build();

        when(dashboardService.getDashboardSummary()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<DashboardSummaryResponse> response = 
            dashboardController.getDashboardSummary();

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(dashboardService).getDashboardSummary();
    }

    @Test
    void getRealtimeMetrics_returnsMetrics() {
        // Arrange
        DashboardMetricsResponse expectedResponse = DashboardMetricsResponse.builder()
                .activeUsers(1500L)
                .onlineDrivers(80L)
                .processingDeliveries(45L)
                .systemLoad(65.5)
                .averageResponseTime(120L)
                .errorRate(0.5)
                .cacheHitRate(95.0)
                .timestamp(LocalDateTime.now())
                .build();

        when(dashboardService.getRealtimeMetrics()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<DashboardMetricsResponse> response = 
            dashboardController.getRealtimeMetrics();

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(dashboardService).getRealtimeMetrics();
    }
}
