package com.sbaldasso.combobackend.modules.payment.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.sbaldasso.combobackend.modules.payment.config.PaymentConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MercadoPagoService {

    private final PaymentConfig paymentConfig;
    private PaymentClient paymentClient;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(paymentConfig.getMercadoPagoAccessToken());
        this.paymentClient = new PaymentClient();
    }

    public String createPixPayment(BigDecimal amount, String description, String payerEmail) {
        try {
            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .transactionAmount(amount.doubleValue())
                    .description(description)
                    .paymentMethodId("pix")
                    .payer(PaymentPayerRequest.builder()
                            .email(payerEmail)
                            .build())
                    .build();

            Payment payment = paymentClient.create(paymentCreateRequest);
            return payment.getId();
        } catch (MPException e) {
            throw new RuntimeException("Erro ao criar pagamento PIX: " + e.getMessage(), e);
        }
    }

    public String createCardPayment(BigDecimal amount, String description, String token, String payerEmail) {
        try {
            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .transactionAmount(amount.doubleValue())
                    .description(description)
                    .paymentMethodId("credit_card")
                    .token(token)
                    .installments(1)
                    .payer(PaymentPayerRequest.builder()
                            .email(payerEmail)
                            .build())
                    .build();

            Payment payment = paymentClient.create(paymentCreateRequest);
            return payment.getId();
        } catch (MPException e) {
            throw new RuntimeException("Erro ao criar pagamento com cartão: " + e.getMessage(), e);
        }
    }

    public Payment.Status getPaymentStatus(String paymentId) {
        try {
            Payment payment = paymentClient.get(paymentId);
            return payment.getStatus();
        } catch (MPException e) {
            throw new RuntimeException("Erro ao consultar status do pagamento: " + e.getMessage(), e);
        }
    }
}
