package com.sbaldasso.combobackend.modules.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbaldasso.combobackend.modules.admin.dto.*;
import com.sbaldasso.combobackend.modules.admin.service.ReportingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReportingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportingService reportingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getWeeklyReport_shouldReturnReport() throws Exception {
        // Arrange
        WeeklyReportResponse mockReport = new WeeklyReportResponse();
        mockReport.setTotalDeliveries(150L);
        mockReport.setTotalRevenue(BigDecimal.valueOf(5000.00));
        mockReport.setAverageDeliveryTime(25.0);
        mockReport.setNewDrivers(10L);

        when(reportingService.generateWeeklyReport(any()))
            .thenReturn(mockReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/reports/weekly")
                .param("startDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeliveries").value(150))
                .andExpect(jsonPath("$.totalRevenue").value(5000.00))
                .andExpect(jsonPath("$.averageDeliveryTime").value(25.0))
                .andExpect(jsonPath("$.newDrivers").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMonthlyReport_shouldReturnReport() throws Exception {
        // Arrange
        MonthlyReportResponse mockReport = new MonthlyReportResponse();
        mockReport.setTotalDeliveries(600L);
        mockReport.setTotalRevenue(BigDecimal.valueOf(20000.00));
        mockReport.setActiveDrivers(50L);
        mockReport.setAverageDriverEarnings(BigDecimal.valueOf(2000.00));

        when(reportingService.generateMonthlyReport(any()))
            .thenReturn(mockReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/reports/monthly")
                .param("yearMonth", YearMonth.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeliveries").value(600))
                .andExpect(jsonPath("$.totalRevenue").value(20000.00))
                .andExpect(jsonPath("$.activeDrivers").value(50))
                .andExpect(jsonPath("$.averageDriverEarnings").value(2000.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRevenueTrends_shouldReturnTrends() throws Exception {
        // Arrange
        RevenueTrendResponse mockTrends = new RevenueTrendResponse();
        List<RevenueTrendData> trendData = List.of(
            new RevenueTrendData(YearMonth.now().minusMonths(2), BigDecimal.valueOf(15000.00)),
            new RevenueTrendData(YearMonth.now().minusMonths(1), BigDecimal.valueOf(18000.00)),
            new RevenueTrendData(YearMonth.now(), BigDecimal.valueOf(20000.00))
        );
        mockTrends.setTrendData(trendData);
        mockTrends.setGrowthRate(BigDecimal.valueOf(15.5));

        when(reportingService.getRevenueTrends(any(), any()))
            .thenReturn(mockTrends);

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/reports/revenue-trends")
                .param("startDate", LocalDate.now().minusMonths(6).toString())
                .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trendData").isArray())
                .andExpect(jsonPath("$.trendData.length()").value(3))
                .andExpect(jsonPath("$.growthRate").value(15.5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDriverEarningsReport_shouldReturnEarningsData() throws Exception {
        // Arrange
        DriverEarningsResponse mockReport = new DriverEarningsResponse();
        mockReport.setDriverId(1L);
        mockReport.setTotalEarnings(BigDecimal.valueOf(3000.00));
        mockReport.setTotalDeliveries(120L);
        mockReport.setAverageEarningsPerTrip(BigDecimal.valueOf(25.00));

        when(reportingService.generateDriverEarningsReport(any(), any(), any()))
            .thenReturn(mockReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/reports/driver-earnings/1")
                .param("startDate", LocalDate.now().minusMonths(1).toString())
                .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(1))
                .andExpect(jsonPath("$.totalEarnings").value(3000.00))
                .andExpect(jsonPath("$.totalDeliveries").value(120))
                .andExpect(jsonPath("$.averageEarningsPerTrip").value(25.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCustomDateRangeReport_shouldReturnCustomReport() throws Exception {
        // Arrange
        CustomDateRangeReportResponse mockReport = new CustomDateRangeReportResponse();
        mockReport.setTotalDeliveries(1000L);
        mockReport.setTotalRevenue(BigDecimal.valueOf(30000.00));
        mockReport.setAverageDeliveryTime(22.5);
        mockReport.setActiveDrivers(75L);

        when(reportingService.generateCustomDateRangeReport(any(), any()))
            .thenReturn(mockReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/reports/custom")
                .param("startDate", LocalDate.now().minusMonths(2).toString())
                .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeliveries").value(1000))
                .andExpect(jsonPath("$.totalRevenue").value(30000.00))
                .andExpect(jsonPath("$.averageDeliveryTime").value(22.5))
                .andExpect(jsonPath("$.activeDrivers").value(75));
    }

    @Test
    void getReports_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reports/weekly")
                .param("startDate", LocalDate.now().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getReports_withInsufficientRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reports/weekly")
                .param("startDate", LocalDate.now().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReports_withInvalidDateRange_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reports/custom")
                .param("startDate", LocalDate.now().toString())
                .param("endDate", LocalDate.now().minusDays(1).toString()))
                .andExpect(status().isBadRequest());
    }
}
