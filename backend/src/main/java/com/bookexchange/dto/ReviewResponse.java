package com.bookexchange.dto;

import com.bookexchange.entity.Review;

import java.time.Instant;

public record ReviewResponse(String id, String exchangeId, String reviewerId, String reviewerName,
                             int rating, String comment, Instant createdAt) {

    public static ReviewResponse from(Review r, String reviewerName) {
        return new ReviewResponse(r.getId(), r.getExchangeId(), r.getReviewerId(), reviewerName,
                r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
