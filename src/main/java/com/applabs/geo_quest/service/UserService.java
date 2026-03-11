/**
 * Service for managing GeoQuest users.
 * <p>
 * Handles user registration, lookup, and persistence.
 * <p>
 * Methods:
 * <ul>
 *   <li><b>registerOrGet</b>: Registers a new user or returns existing.</li>
 *   <li><b>findById</b>: Finds a user by UID.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers for user authentication and onboarding.</li>
 *   <li>Persists user info for access control and display.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.applabs.geo_quest.dto.request.UserRegistrationRequest;
import com.applabs.geo_quest.model.User;
import com.applabs.geo_quest.repository.UserRepository;

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
