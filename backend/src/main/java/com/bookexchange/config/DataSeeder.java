package com.bookexchange.config;

import com.bookexchange.entity.Category;
import com.bookexchange.entity.User;
import com.bookexchange.repository.CategoryRepository;
import com.bookexchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataSeeder(UserRepository userRepository, CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String email = adminEmail.toLowerCase();
        if (!userRepository.existsByEmail(email)) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setCity("Mumbai");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
        if (categoryRepository.count() == 0) {
            for (String name : List.of("Engineering", "Science", "Mathematics", "Competitive Exams",
                    "Novels", "Fiction", "Non-Fiction", "Self-Help", "Comics", "Others")) {
                categoryRepository.save(new Category(name));
            }
        }
    }
}
