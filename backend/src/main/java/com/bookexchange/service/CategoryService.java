package com.bookexchange.service;

import com.bookexchange.entity.Category;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    public List<Category> list() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    public Category create(String name) {
        String clean = name.trim();
        if (categoryRepository.existsByNameIgnoreCase(clean)) {
            throw ApiException.conflict("Category already exists");
        }
        return categoryRepository.save(new Category(clean));
    }

    public Category update(String id, String name) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> ApiException.notFound("Category not found"));
        String clean = name.trim();
        if (!c.getName().equalsIgnoreCase(clean) && categoryRepository.existsByNameIgnoreCase(clean)) {
            throw ApiException.conflict("Category already exists");
        }
        c.setName(clean);
        return categoryRepository.save(c);
    }

    public void delete(String id) {
        if (!categoryRepository.existsById(id)) {
            throw ApiException.notFound("Category not found");
        }
        if (bookRepository.countByCategoryId(id) > 0) {
            throw ApiException.conflict("Books are using this category, so it cannot be deleted");
        }
        categoryRepository.deleteById(id);
    }
}
