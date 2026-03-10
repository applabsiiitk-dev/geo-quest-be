package com.applabs.geo_quest.service;

import com.applabs.geo_quest.dto.request.UserRegistrationRequest;
import com.applabs.geo_quest.model.User;
import com.applabs.geo_quest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerOrGet(String uid, UserRegistrationRequest request) {
        return userRepository.findById(uid).orElseGet(() -> {
            User newUser = User.builder()
                    .uid(uid)
                    .email(request.getEmail())
                    .displayName(request.getDisplayName())
                    .photoUrl(request.getPhotoUrl())
                    .createdAt(Instant.now())
                    .build();
            return userRepository.save(newUser);
        });
    }

    public Optional<User> findById(String uid) {
        return userRepository.findById(uid);
    }
}
