package com.sbaldasso.combobackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbaldasso.combobackend.config.BCrypt;
import com.sbaldasso.combobackend.domain.Role;
import com.sbaldasso.combobackend.domain.User;
import com.sbaldasso.combobackend.dtos.UserDto;
import com.sbaldasso.combobackend.repositories.RoleRepository;
import com.sbaldasso.combobackend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private BCrypt passwordEncoder;

  public User createUser(UserDto userDto) {
    User user = new User();
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.bCrypt().encode(userDto.getPassword()));
    user.setUsername(userDto.getUsername());

    // Handle role assignment
    String roleName = userDto.isAdmin() ? "ROLE_ADMIN" : "ROLE_CUSTOMER";
    Role role = roleRepository.findByName(roleName)
        .orElseGet(() -> {
          Role newRole = new Role(roleName);
          return roleRepository.save(newRole);
        });

    user.setRole(role);
    return userRepository.save(user);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  public User updateUser(Long id, UserDto userDto) {
    User user = userRepository.findById(id).orElseThrow();
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.bCrypt().encode(userDto.getPassword()));
    user.setUsername(userDto.getUsername());

    // Handle role update
    String roleName = userDto.isAdmin() ? "ROLE_ADMIN" : "ROLE_CUSTOMER";
    Role role = roleRepository.findByName(roleName)
        .orElseGet(() -> {
          Role newRole = new Role(roleName);
          return roleRepository.save(newRole);
        });

    user.setRole(role);
    return userRepository.save(user);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }
}
