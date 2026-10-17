package com.bookexchange.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequest(
        @NotBlank(message = "Name is required") String name,
        String phone,
        @NotBlank(message = "City is required") String city) {
}
