package com.sbaldasso.combobackend.modules.payment.repository;

import com.sbaldasso.combobackend.modules.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByStatusAndCreatedAtAfter(Payment.PaymentStatus status, LocalDateTime createdAt);
}
