package com.bookexchange.repository;

import com.bookexchange.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {
    boolean existsByNameIgnoreCase(String name);
}
