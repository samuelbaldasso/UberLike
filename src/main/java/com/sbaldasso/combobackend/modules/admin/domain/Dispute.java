package com.sbaldasso.combobackend.modules.admin.domain;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "disputes")
@Data
@NoArgsConstructor
public class Dispute {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DisputeType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DisputeStatus status;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(length = 1000)
    private String resolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolver_id")
    private User resolver;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum DisputeType {
        DELIVERY_NOT_COMPLETED,
        CANCELLATION_FEE,
        PAYMENT_ISSUE,
        INAPPROPRIATE_BEHAVIOR,
        OTHER
    }

    public enum DisputeStatus {
        PENDING,
        IN_REVIEW,
        RESOLVED,
        CLOSED
    }
}
