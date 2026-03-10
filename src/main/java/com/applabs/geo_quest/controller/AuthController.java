package com.applabs.geo_quest.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.applabs.geo_quest.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final String googleClientId;
    private static final String ALLOWED_DOMAIN = "iiitkottayam.ac.in";

    public AuthController(JwtUtil jwtUtil,
                          @Value("${app.google.client-id}") String googleClientId) {
        this.jwtUtil = jwtUtil;
        this.googleClientId = googleClientId;
    }

    /**
     * POST /api/auth/google
     * Flutter sends the Google ID token after sign-in.
     * Server verifies it, checks domain, and returns its own JWT.
     *
     * Request:  { "idToken": "<google_id_token>" }
     * Response: { "token": "<your_jwt>", "uid": "...", "email": "..." }
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) throws Exception {
        String idToken = body.get("idToken");
        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "idToken is required"));
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleToken = verifier.verify(idToken);
        if (googleToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Google token"));
        }

        GoogleIdToken.Payload payload = googleToken.getPayload();
        String email = payload.getEmail();
        String uid = payload.getSubject(); // Google's unique user ID

        // Domain check
        if (email == null || !email.endsWith("@" + ALLOWED_DOMAIN)) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Access restricted to @" + ALLOWED_DOMAIN + " accounts"
            ));
        }

        String jwt = jwtUtil.generate(uid, email);
        return ResponseEntity.ok(Map.of("token", jwt, "uid", uid, "email", email));
    }
}