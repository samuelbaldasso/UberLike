package com.sbaldasso.combobackend.modules.payment.controller;

import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import com.sbaldasso.combobackend.modules.payment.dto.PaymentResponse;
import com.sbaldasso.combobackend.modules.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/deliveries/{deliveryId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable UUID deliveryId,
            @RequestParam Payment.PaymentMethod method) {
        return ResponseEntity.ok(paymentService.createPayment(deliveryId, method));
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }
}
