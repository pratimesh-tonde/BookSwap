package com.bookexchange.dto;

import jakarta.validation.constraints.NotBlank;

public record ExchangeRequestDto(
        @NotBlank(message = "Requested book is required") String requestedBookId,
        @NotBlank(message = "Choose a book to offer") String offeredBookId) {
}
