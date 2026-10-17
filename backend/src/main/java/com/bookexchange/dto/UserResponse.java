package com.bookexchange.dto;

import com.bookexchange.entity.User;

public record UserResponse(String id, String name, String email, String phone, String city,
                           String role, boolean blocked, double avgRating, int ratingCount) {

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getCity(),
                u.getRole(), u.isBlocked(), u.getAvgRating(), u.getRatingCount());
    }
}
