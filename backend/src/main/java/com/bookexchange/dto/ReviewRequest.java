package com.bookexchange.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
        @NotBlank(message = "Exchange is required") String exchangeId,
        @Min(value = 1, message = "Rating must be between 1 and 5") @Max(value = 5, message = "Rating must be between 1 and 5") int rating,
        String comment) {
}
