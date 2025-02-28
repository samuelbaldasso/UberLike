package com.sbaldasso.combobackend.modules.user.service;

import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.domain.UserType;

import java.util.UUID;

public interface UserService {
  User validateAndGetUser(UUID userId);

  User validateAndGetUser(UUID userId, UserType expectedType);

  boolean isUserActive(UUID userId);
}