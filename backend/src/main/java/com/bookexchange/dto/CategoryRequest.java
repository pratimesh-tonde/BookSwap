package com.bookexchange.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(@NotBlank(message = "Category name is required") String name) {
}
