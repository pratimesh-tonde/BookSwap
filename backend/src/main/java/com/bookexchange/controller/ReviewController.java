package com.bookexchange.controller;

import com.bookexchange.dto.ReviewRequest;
import com.bookexchange.dto.ReviewResponse;
import com.bookexchange.entity.User;
import com.bookexchange.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(@AuthenticationPrincipal User user, @Valid @RequestBody ReviewRequest req) {
        return reviewService.create(user, req);
    }
}
