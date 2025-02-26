package com.sbaldasso.combobackend.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sbaldasso.combobackend.domain.Role;
import com.sbaldasso.combobackend.domain.User;
import com.sbaldasso.combobackend.repositories.UserRepository;

@Service
public class UserDetailsServiceCustom implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User newUser = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found."));

		if (newUser != null) {
			return new org.springframework.security.core.userdetails.User(newUser.getEmail(), newUser.getPassword(),
					rolesMapper(newUser.getRole()));

		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

	private Collection<? extends GrantedAuthority> rolesMapper(Role role) {
		return Collections.singletonList(new SimpleGrantedAuthority(role.getName()));
	}
}
