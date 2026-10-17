package com.bookexchange.service;

import com.bookexchange.dto.ProfileUpdateRequest;
import com.bookexchange.dto.UserResponse;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse me(User principal) {
        User fresh = userRepository.findById(principal.getId())
                .orElseThrow(() -> ApiException.notFound("User not found"));
        return UserResponse.from(fresh);
    }

    public UserResponse update(User principal, ProfileUpdateRequest req) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> ApiException.notFound("User not found"));
        user.setName(req.name().trim());
        user.setPhone(req.phone());
        user.setCity(req.city().trim());
        userRepository.save(user);
        return UserResponse.from(user);
    }
}
