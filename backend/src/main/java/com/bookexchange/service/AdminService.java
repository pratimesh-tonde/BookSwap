package com.bookexchange.service;

import com.bookexchange.dto.StatsResponse;
import com.bookexchange.dto.UserResponse;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.CategoryRepository;
import com.bookexchange.repository.ExchangeRepository;
import com.bookexchange.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ExchangeRepository exchangeRepository;
    private final CategoryRepository categoryRepository;

    public AdminService(UserRepository userRepository, BookRepository bookRepository,
                        ExchangeRepository exchangeRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.exchangeRepository = exchangeRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<UserResponse> users() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream().map(UserResponse::from).toList();
    }

    /** Blocks the user if active, unblocks if blocked. */
    public UserResponse toggleBlock(User admin, String userId) {
        User target = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));
        if (target.getId().equals(admin.getId()) || "ADMIN".equals(target.getRole())) {
            throw ApiException.badRequest("Admin accounts cannot be blocked");
        }
        target.setBlocked(!target.isBlocked());
        userRepository.save(target);
        return UserResponse.from(target);
    }

    public StatsResponse stats() {
        long totalBooks = bookRepository.count();
        long available = bookRepository.countByStatus("AVAILABLE");
        return new StatsResponse(
                userRepository.count(),
                totalBooks,
                available,
                totalBooks - available,
                exchangeRepository.count(),
                exchangeRepository.countByStatus("PENDING"),
                exchangeRepository.countByStatus("ACCEPTED"),
                exchangeRepository.countByStatus("REJECTED"),
                categoryRepository.count());
    }
}
