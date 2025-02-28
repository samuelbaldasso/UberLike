package com.sbaldasso.combobackend.modules.location.domain;

import com.sbaldasso.combobackend.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "locations")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "driver_id", nullable = false)
  private User driver;

  @Column(nullable = false)
  private Double latitude;

  @Column(nullable = false)
  private Double longitude;

  @CreatedDate
  private LocalDateTime timestamp;

  private Double speed;

  private Double heading;

  @Column(nullable = false)
  private boolean isAvailable = true;
}