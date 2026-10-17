package com.bookexchange.dto;

public record AuthResponse(String token, UserResponse user) {
}
