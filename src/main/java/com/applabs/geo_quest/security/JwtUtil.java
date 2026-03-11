/**
 * Utility class for JWT token generation and parsing in GeoQuest.
 * <p>
 * Handles creation and validation of JWT tokens for user authentication.
 * <p>
 * Methods:
 * <ul>
 *   <li><b>generate</b>: Generates a JWT token for a user.</li>
 *   <li><b>parse</b>: Parses and validates a JWT token.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by JwtAuthenticationFilter for authentication.</li>
 *   <li>Tokens include user ID and email, expire after 150 minutes.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey key;
    private static final long EXPIRY_MS = 150 * 60 * 1000L; // 150 minutes

    public JwtUtil(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generate(String uid, String email) {
        return Jwts.builder()
                .subject(uid)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}