package com.bookexchange.controller;

import com.bookexchange.dto.ProfileUpdateRequest;
import com.bookexchange.dto.ReviewResponse;
import com.bookexchange.dto.UserResponse;
import com.bookexchange.entity.User;
import com.bookexchange.service.ReviewService;
import com.bookexchange.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;

    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return userService.me(user);
    }

    @PutMapping("/me")
    public UserResponse update(@AuthenticationPrincipal User user, @Valid @RequestBody ProfileUpdateRequest req) {
        return userService.update(user, req);
    }

    @GetMapping("/{id}/reviews")
    public List<ReviewResponse> reviews(@PathVariable String id) {
        return reviewService.forUser(id);
    }
}
