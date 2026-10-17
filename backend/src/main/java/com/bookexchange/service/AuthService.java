package com.bookexchange.service;

import com.bookexchange.dto.AuthResponse;
import com.bookexchange.dto.LoginRequest;
import com.bookexchange.dto.RegisterRequest;
import com.bookexchange.dto.UserResponse;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.UserRepository;
import com.bookexchange.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw ApiException.conflict("An account with this email already exists");
        }
        User user = new User();
        user.setName(req.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setPhone(req.phone());
        user.setCity(req.city().trim());
        user.setRole("USER");
        userRepository.save(user);
        return new AuthResponse(jwtUtil.generate(user), UserResponse.from(user));
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw ApiException.unauthorized("Invalid email or password");
        }
        if (user.isBlocked()) {
            throw ApiException.forbidden("Your account has been blocked by the admin");
        }
        return new AuthResponse(jwtUtil.generate(user), UserResponse.from(user));
    }
}
