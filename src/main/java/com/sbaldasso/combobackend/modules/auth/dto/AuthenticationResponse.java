package com.sbaldasso.combobackend.modules.auth.dto;

import com.sbaldasso.combobackend.modules.user.dto.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
  private String token;
  private UserResponse user;
}