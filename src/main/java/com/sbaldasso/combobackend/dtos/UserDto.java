package com.sbaldasso.combobackend.dtos;

public record UserDto(String username, String password, String email, boolean isAdmin) {
	
}
