package com.sbaldasso.combobackend.modules.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FinancialReportSummary {
    private LocalDate date;
    private long totalDeliveries;
    private long completedDeliveries;
    private long cancelledDeliveries;
    private BigDecimal totalRevenue;
    private BigDecimal platformFees;
    private BigDecimal driversPayouts;
    private BigDecimal cancellationFees;
    private double completionRate;
    private BigDecimal averageDeliveryValue;
}
