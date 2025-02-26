package com.sbaldasso.combobackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbaldasso.combobackend.config.BCrypt;
import com.sbaldasso.combobackend.domain.Role;
import com.sbaldasso.combobackend.domain.User;
import com.sbaldasso.combobackend.dtos.UserDto;
import com.sbaldasso.combobackend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCrypt passwordEncoder;

    public User createUser(UserDto userDto) {
    	User user = new User();
    	user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.bCrypt().encode(userDto.password()));
        user.setRole(userDto.isAdmin() ? new Role("ADMIN") : new Role("CUSTOMER"));
        user.setUsername(userDto.username());
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
    	user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.bCrypt().encode(userDto.password()));
        user.setRole(userDto.isAdmin() ? new Role("ADMIN") : new Role("CUSTOMER"));
        user.setUsername(userDto.username());
        
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
