package com.sbaldasso.combobackend.modules.payment.service;

import com.mercadopago.resources.payment.Payment.Status;
import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.service.DeliveryService;
import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import com.sbaldasso.combobackend.modules.payment.dto.PaymentCalculationResult;
import com.sbaldasso.combobackend.modules.payment.dto.PaymentResponse;
import com.sbaldasso.combobackend.modules.payment.repository.PaymentRepository;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCalculationService calculationService;
    private final MercadoPagoService mercadoPagoService;
    private final DeliveryService deliveryService;
    private final UserService userService;

    @Transactional
    public PaymentResponse createPayment(UUID deliveryId, Payment.PaymentMethod method) {
        Delivery delivery = deliveryService.getDeliveryById(deliveryId);
        
        // Calcula o valor da corrida
        PaymentCalculationResult calculation = calculationService.calculatePayment(
            delivery.getDistanceInKm(),
            delivery.getEstimatedTimeInMinutes()
        );

        // Cria o registro de pagamento
        Payment payment = new Payment();
        payment.setDelivery(delivery);
        payment.setMethod(method);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setAmount(calculation.getTotalAmount());
        payment.setDriverAmount(calculation.getDriverAmount());
        payment.setPlatformFee(calculation.getPlatformFee());

        payment = paymentRepository.save(payment);

        // Inicia o processamento do pagamento
        try {
            String transactionId = processPayment(payment, delivery.getCustomer());
            payment.setTransactionId(transactionId);
            payment.setStatus(Payment.PaymentStatus.PROCESSING);
            payment = paymentRepository.save(payment);
        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw e;
        }

        return calculationService.toPaymentResponse(payment);
    }

    private String processPayment(Payment payment, User customer) {
        String description = "Entrega #" + payment.getDelivery().getId();
        
        if (payment.getMethod() == Payment.PaymentMethod.PIX) {
            return mercadoPagoService.createPixPayment(
                payment.getAmount(),
                description,
                customer.getEmail()
            );
        } else {
            throw new UnsupportedOperationException("Método de pagamento não suportado");
        }
    }

    @Transactional
    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));
        return calculationService.toPaymentResponse(payment);
    }

    @Scheduled(fixedDelay = 60000) // Executa a cada minuto
    @Transactional
    public void checkPendingPayments() {
        List<Payment> processingPayments = paymentRepository
            .findByStatusAndCreatedAtAfter(
                Payment.PaymentStatus.PROCESSING,
                LocalDateTime.now().minusHours(24)
            );

        for (Payment payment : processingPayments) {
            try {
                Status mpStatus = mercadoPagoService.getPaymentStatus(payment.getTransactionId());
                updatePaymentStatus(payment, mpStatus);
            } catch (Exception e) {
                // Log error but continue processing other payments
                e.printStackTrace();
            }
        }
    }

    private void updatePaymentStatus(Payment payment, Status mpStatus) {
        switch (mpStatus) {
            case APPROVED:
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                break;
            case REJECTED:
                payment.setStatus(Payment.PaymentStatus.FAILED);
                break;
            case REFUNDED:
                payment.setStatus(Payment.PaymentStatus.REFUNDED);
                break;
            default:
                // Keep current status for other cases
                return;
        }
        paymentRepository.save(payment);
    }
}
