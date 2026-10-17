package com.bookexchange.dto;

import java.time.Instant;

public record ExchangeResponse(String id, String status, Instant createdAt, Instant updatedAt,
                               String requesterId, String requesterName,
                               String ownerId, String ownerName,
                               String requestedBookId, String requestedBookTitle, String requestedBookImage,
                               String offeredBookId, String offeredBookTitle, String offeredBookImage,
                               boolean reviewedByMe) {
}
