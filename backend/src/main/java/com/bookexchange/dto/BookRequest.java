package com.bookexchange.dto;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Author is required") String author,
        @NotBlank(message = "Category is required") String categoryId,
        @NotBlank(message = "Condition is required") String condition,
        String description,
        String imageUrl) {
}
