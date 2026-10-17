package com.bookexchange.service;

import com.bookexchange.dto.ReviewRequest;
import com.bookexchange.dto.ReviewResponse;
import com.bookexchange.entity.Exchange;
import com.bookexchange.entity.Review;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.ExchangeRepository;
import com.bookexchange.repository.ReviewRepository;
import com.bookexchange.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ReviewService(ReviewRepository reviewRepository, ExchangeRepository exchangeRepository,
                         UserRepository userRepository, NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.exchangeRepository = exchangeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public ReviewResponse create(User user, ReviewRequest req) {
        Exchange ex = exchangeRepository.findById(req.exchangeId())
                .orElseThrow(() -> ApiException.notFound("Exchange not found"));
        boolean isRequester = ex.getRequesterId().equals(user.getId());
        boolean isOwner = ex.getOwnerId().equals(user.getId());
        if (!isRequester && !isOwner) {
            throw ApiException.forbidden("You are not part of this exchange");
        }
        if (!"ACCEPTED".equals(ex.getStatus())) {
            throw ApiException.badRequest("You can review only after the exchange is accepted");
        }
        if (reviewRepository.existsByExchangeIdAndReviewerId(ex.getId(), user.getId())) {
            throw ApiException.conflict("You have already reviewed this exchange");
        }

        String otherId = isRequester ? ex.getOwnerId() : ex.getRequesterId();
        Review review = new Review();
        review.setExchangeId(ex.getId());
        review.setReviewerId(user.getId());
        review.setReviewedUserId(otherId);
        review.setRating(req.rating());
        review.setComment(req.comment());
        review.setCreatedAt(Instant.now());
        reviewRepository.save(review);

        // keep the average rating on the user profile up to date
        List<Review> all = reviewRepository.findByReviewedUserIdOrderByCreatedAtDesc(otherId);
        double avg = all.stream().mapToInt(Review::getRating).average().orElse(0);
        User other = userRepository.findById(otherId).orElseThrow(() -> ApiException.notFound("User not found"));
        other.setAvgRating(Math.round(avg * 10) / 10.0);
        other.setRatingCount(all.size());
        userRepository.save(other);

        notificationService.notify(otherId, user.getName() + " rated you " + req.rating() + " out of 5.");
        return ReviewResponse.from(review, user.getName());
    }

    public List<ReviewResponse> forUser(String userId) {
        List<Review> reviews = reviewRepository.findByReviewedUserIdOrderByCreatedAtDesc(userId);
        Map<String, String> names = userRepository
                .findAllById(reviews.stream().map(Review::getReviewerId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(User::getId, User::getName, (a, b) -> a));
        return reviews.stream()
                .map(r -> ReviewResponse.from(r, names.getOrDefault(r.getReviewerId(), "Unknown user")))
                .toList();
    }
}
