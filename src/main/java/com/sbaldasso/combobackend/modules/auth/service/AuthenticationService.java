package com.sbaldasso.combobackend.modules.auth.service;

import com.sbaldasso.combobackend.modules.auth.dto.AuthenticationRequest;
import com.sbaldasso.combobackend.modules.auth.dto.AuthenticationResponse;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.repository.UserRepository;
import com.sbaldasso.combobackend.modules.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final UserServiceImpl userService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()));

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

    if (!user.isActive()) {
      throw new IllegalStateException("User account is not active");
    }

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        user.getId().toString(),
        user.getPassword(),
        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())));

    String token = jwtService.generateToken(userDetails);

    return AuthenticationResponse.builder()
        .token(token)
        .user(userService.getUserById(user.getId()))
        .build();
  }
}