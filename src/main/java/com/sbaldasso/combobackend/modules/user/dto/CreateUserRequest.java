package com.sbaldasso.combobackend.modules.user.dto;

import com.sbaldasso.combobackend.modules.user.domain.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {
  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

  @NotBlank(message = "Phone is required")
  private String phone;

  @NotNull(message = "User type is required")
  private UserType userType;
}