package com.sbaldasso.combobackend.modules.user.dto;

import com.sbaldasso.combobackend.modules.user.domain.UserType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
  private UUID id;
  private String name;
  private String email;
  private String phone;
  private UserType userType;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}