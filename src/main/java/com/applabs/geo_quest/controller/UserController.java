// PLAN: Riddle-based hints implementation
// - User logic may need to be aware of hint changes for question progress
package com.applabs.geo_quest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.applabs.geo_quest.dto.request.UserRegistrationRequest;
import com.applabs.geo_quest.model.User;
import com.applabs.geo_quest.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
/**
 * Controller for user profile management endpoints in GeoQuest.
 * <p>
 * Handles user registration and retrieval of the authenticated user's profile.
 * Delegates user creation and lookup to UserService.
 * <p>
 * Endpoints:
 * <ul>
 * <li>POST /api/users/register — Register or retrieve user profile</li>
 * <li>GET /api/users/me — Get current user's profile</li>
 * </ul>
 * <p>
 * Registration is triggered after Expo AuthSession OAuth authentication from the client.
 *
 * @author fl4nk3r
 */
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/users/register
     * Called right after Expo AuthSession OAuth authentication.
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
