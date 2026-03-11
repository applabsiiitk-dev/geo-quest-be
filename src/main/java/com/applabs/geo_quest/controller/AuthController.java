package com.applabs.geo_quest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.applabs.geo_quest.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final GoogleIdTokenVerifier verifier; // inject the verifier bean
    private final String googleClientId;
    private static final String ALLOWED_DOMAIN = "iiitkottayam.ac.in";

    // ← ADD verifier parameter to constructor
    public AuthController(JwtUtil jwtUtil,
            GoogleIdTokenVerifier verifier,
            @Value("${app.google.client-id}") String googleClientId) {
        this.jwtUtil = jwtUtil;
        this.verifier = verifier; // Spring injects the bean from GoogleAuthConfig
        this.googleClientId = googleClientId;
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) throws Exception {
        String idToken = body.get("idToken");
        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "idToken is required"));
        }

        GoogleIdToken googleToken = verifier.verify(idToken); // uses the injected singleton
        if (googleToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Google token"));
        }

        GoogleIdToken.Payload payload = googleToken.getPayload();
        String email = payload.getEmail();
        String uid = payload.getSubject();

        if (email == null || !email.endsWith("@" + ALLOWED_DOMAIN)) {
            return ResponseEntity.status(403).body(Map.of(
                    "error", "Access restricted to @" + ALLOWED_DOMAIN + " accounts"));
        }

        String jwt = jwtUtil.generate(uid, email);
        return ResponseEntity.ok(Map.of("token", jwt, "uid", uid, "email", email));
    }
}