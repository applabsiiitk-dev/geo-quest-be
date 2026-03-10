package com.applabs.geo_quest.controller;

import com.applabs.geo_quest.dto.request.UserRegistrationRequest;
import com.applabs.geo_quest.model.User;
import com.applabs.geo_quest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/users/register
     * Called right after Firebase sign-in from Flutter.
     * Creates the user profile if it doesn't exist, returns it if it does.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerOrGet(
            @AuthenticationPrincipal String uid,
            @Valid @RequestBody UserRegistrationRequest request) {
        User user = userService.registerOrGet(uid, request);
        return ResponseEntity.ok(user);
    }

    /**
     * GET /api/users/me
     * Returns the profile of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getMe(@AuthenticationPrincipal String uid) {
        return userService.findById(uid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
