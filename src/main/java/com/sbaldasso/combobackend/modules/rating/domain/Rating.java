package com.sbaldasso.combobackend.modules.rating.domain;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ratings")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "delivery_id", nullable = false)
  private Delivery delivery;

  @ManyToOne
  @JoinColumn(name = "from_user_id", nullable = false)
  private User fromUser;

  @ManyToOne
  @JoinColumn(name = "to_user_id", nullable = false)
  private User toUser;

  @Column(nullable = false)
  private Integer rating;

  private String comment;

  @CreatedDate
  private LocalDateTime createdAt;

  @PrePersist
  public void validateRating() {
    if (rating < 1 || rating > 5) {
      throw new IllegalArgumentException("Rating must be between 1 and 5");
    }
  }
}