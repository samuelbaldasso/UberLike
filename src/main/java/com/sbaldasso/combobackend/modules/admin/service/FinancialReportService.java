package com.sbaldasso.combobackend.modules.admin.service;

import com.sbaldasso.combobackend.modules.admin.dto.FinancialReportSummary;
import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.delivery.repository.DeliveryRepository;
import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import com.sbaldasso.combobackend.modules.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class FinancialReportService {

    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public FinancialReportSummary generateDailyReport(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Estatísticas de entregas
        long totalDeliveries = deliveryRepository.countByCreatedAtBetween(startOfDay, endOfDay);
        long completedDeliveries = deliveryRepository.countByStatusAndCreatedAtBetween(
                DeliveryStatus.FINISHED, startOfDay, endOfDay);
        long cancelledDeliveries = deliveryRepository.countByStatusAndCreatedAtBetween(
                DeliveryStatus.CANCELLED, startOfDay, endOfDay);

        // Estatísticas financeiras
        BigDecimal totalRevenue = paymentRepository.sumAmountByDateAndStatus(
                startOfDay, endOfDay, Payment.PaymentStatus.COMPLETED);
        BigDecimal platformFees = paymentRepository.sumPlatformFeesByDateAndStatus(
                startOfDay, endOfDay, Payment.PaymentStatus.COMPLETED);
        BigDecimal driversPayouts = paymentRepository.sumDriverAmountByDateAndStatus(
                startOfDay, endOfDay, Payment.PaymentStatus.COMPLETED);
        BigDecimal cancellationFees = paymentRepository.sumCancellationFeesByDate(startOfDay, endOfDay);

        // Cálculos derivados
        double completionRate = totalDeliveries > 0 ?
                (double) completedDeliveries / totalDeliveries * 100 : 0;
        BigDecimal averageDeliveryValue = completedDeliveries > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(completedDeliveries), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        return FinancialReportSummary.builder()
                .date(date)
                .totalDeliveries(totalDeliveries)
                .completedDeliveries(completedDeliveries)
                .cancelledDeliveries(cancelledDeliveries)
                .totalRevenue(totalRevenue)
                .platformFees(platformFees)
                .driversPayouts(driversPayouts)
                .cancellationFees(cancellationFees)
                .completionRate(completionRate)
                .averageDeliveryValue(averageDeliveryValue)
                .build();
    }
}
