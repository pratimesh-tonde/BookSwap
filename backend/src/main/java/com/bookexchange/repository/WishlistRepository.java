package com.bookexchange.repository;

import com.bookexchange.entity.WishlistItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WishlistRepository extends MongoRepository<WishlistItem, String> {
    List<WishlistItem> findByUserId(String userId);
    boolean existsByUserIdAndBookId(String userId, String bookId);
    void deleteByUserIdAndBookId(String userId, String bookId);
}
