package com.sbaldasso.combobackend.modules.admin.service;

import com.sbaldasso.combobackend.modules.admin.dto.DashboardMetricsResponse;
import com.sbaldasso.combobackend.modules.admin.dto.DashboardSummaryResponse;
import com.sbaldasso.combobackend.modules.admin.repository.DisputeRepository;
import com.sbaldasso.combobackend.modules.admin.repository.DriverApprovalRepository;
import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.delivery.repository.DeliveryRepository;
import com.sbaldasso.combobackend.modules.location.repository.LocationRepository;
import com.sbaldasso.combobackend.modules.monitoring.SystemHealthService;
import com.sbaldasso.combobackend.modules.payment.repository.PaymentRepository;
import com.sbaldasso.combobackend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private DisputeRepository disputeRepository;

    @Mock
    private DriverApprovalRepository driverApprovalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SystemHealthService systemHealthService;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDashboardSummary_returnsSummaryWithMetrics() {
        // Arrange
        when(deliveryRepository.countByStatus(DeliveryStatus.IN_PROGRESS))
                .thenReturn(50L);
        when(locationRepository.countAvailableDrivers())
                .thenReturn(100L);
        when(driverApprovalRepository.countPendingApprovals())
                .thenReturn(10L);
        when(disputeRepository.countOpenDisputes())
                .thenReturn(5L);
        when(paymentRepository.calculateDailyRevenue(any()))
                .thenReturn(BigDecimal.valueOf(5000.00));
        when(deliveryRepository.countDailyDeliveries(any()))
                .thenReturn(200L);
        when(deliveryRepository.calculateAverageDeliveryTime(any()))
                .thenReturn(25.0);
        when(systemHealthService.getSystemHealth())
                .thenReturn("HEALTHY");

        // Act
        DashboardSummaryResponse response = dashboardService.getDashboardSummary();

        // Assert
        assertNotNull(response);
        assertEquals(50L, response.getActiveDeliveries());
        assertEquals(100L, response.getAvailableDrivers());
        assertEquals(10L, response.getPendingApprovals());
        assertEquals(5L, response.getOpenDisputes());
        assertEquals(BigDecimal.valueOf(5000.00), response.getDailyRevenue());
        assertEquals(200L, response.getDailyDeliveries());
        assertEquals(25.0, response.getAverageDeliveryTime());
        assertEquals("HEALTHY", response.getSystemHealth());
        assertNotNull(response.getLastUpdated());
    }

    @Test
    void getRealtimeMetrics_returnsCurrentMetrics() {
        // Arrange
        when(userRepository.countActiveUsers())
                .thenReturn(1500L);
        when(locationRepository.countOnlineDrivers())
                .thenReturn(80L);
        when(deliveryRepository.countProcessingDeliveries())
                .thenReturn(45L);
        when(systemHealthService.getSystemLoad())
                .thenReturn(65.5);
        when(systemHealthService.getAverageResponseTime())
                .thenReturn(120L);
        when(systemHealthService.getErrorRate())
                .thenReturn(0.5);
        when(systemHealthService.getCacheHitRate())
                .thenReturn(95.0);

        // Act
        DashboardMetricsResponse response = dashboardService.getRealtimeMetrics();

        // Assert
        assertNotNull(response);
        assertEquals(1500L, response.getActiveUsers());
        assertEquals(80L, response.getOnlineDrivers());
        assertEquals(45L, response.getProcessingDeliveries());
        assertEquals(65.5, response.getSystemLoad());
        assertEquals(120L, response.getAverageResponseTime());
        assertEquals(0.5, response.getErrorRate());
        assertEquals(95.0, response.getCacheHitRate());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void getDashboardSummary_handlesEmptyMetrics() {
        // Arrange
        when(systemHealthService.getSystemHealth())
                .thenReturn("DEGRADED");

        // Act
        DashboardSummaryResponse response = dashboardService.getDashboardSummary();

        // Assert
        assertNotNull(response);
        assertEquals(0L, response.getActiveDeliveries());
        assertEquals(0L, response.getAvailableDrivers());
        assertEquals(0L, response.getPendingApprovals());
        assertEquals(0L, response.getOpenDisputes());
        assertEquals(BigDecimal.ZERO, response.getDailyRevenue());
        assertEquals(0L, response.getDailyDeliveries());
        assertEquals(0.0, response.getAverageDeliveryTime());
        assertEquals("DEGRADED", response.getSystemHealth());
    }

    @Test
    void getRealtimeMetrics_handlesDegradedService() {
        // Arrange
        when(systemHealthService.getSystemLoad())
                .thenReturn(95.5);
        when(systemHealthService.getErrorRate())
                .thenReturn(5.0);
        when(systemHealthService.getCacheHitRate())
                .thenReturn(50.0);

        // Act
        DashboardMetricsResponse response = dashboardService.getRealtimeMetrics();

        // Assert
        assertNotNull(response);
        assertTrue(response.getSystemLoad() > 90.0);
        assertTrue(response.getErrorRate() > 1.0);
        assertTrue(response.getCacheHitRate() < 80.0);
    }
}
