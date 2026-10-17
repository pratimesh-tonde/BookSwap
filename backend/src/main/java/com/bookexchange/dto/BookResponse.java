package com.bookexchange.dto;

import com.bookexchange.entity.Book;
import com.bookexchange.entity.Category;
import com.bookexchange.entity.User;

import java.time.Instant;

public record BookResponse(String id, String title, String author, String categoryId, String categoryName,
                           String condition, String description, String imageUrl,
                           String ownerId, String ownerName, String ownerCity, double ownerRating, int ownerRatingCount,
                           String status, Instant createdAt) {

    public static BookResponse from(Book b, User owner, Category category) {
        return new BookResponse(
                b.getId(), b.getTitle(), b.getAuthor(),
                b.getCategoryId(), category == null ? null : category.getName(),
                b.getCondition(), b.getDescription(), b.getImageUrl(),
                b.getOwnerId(),
                owner == null ? "Unknown user" : owner.getName(),
                owner == null ? b.getCity() : owner.getCity(),
                owner == null ? 0 : owner.getAvgRating(),
                owner == null ? 0 : owner.getRatingCount(),
                b.getStatus(), b.getCreatedAt());
    }
}