package com.bookexchange.repository;

import com.bookexchange.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findByOwnerIdOrderByCreatedAtDesc(String ownerId);
    long countByCategoryId(String categoryId);
    long countByStatus(String status);
}
