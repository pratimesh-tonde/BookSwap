package com.bookexchange.repository;

import com.bookexchange.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByReviewedUserIdOrderByCreatedAtDesc(String reviewedUserId);
    boolean existsByExchangeIdAndReviewerId(String exchangeId, String reviewerId);
}
