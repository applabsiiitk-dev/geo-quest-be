package com.applabs.geo_quest.security;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

/**
 * Simple in-memory rate limiter for GeoQuest endpoints (e.g.,
 * /questions/trigger).
 * <p>
 * Prevents users from spamming proximity triggers faster than 5 seconds.
 * <p>
 * Key features:
 * <ul>
 * <li>Atomic check and update per user ID</li>
 * <li>Per-instance storage (use Redis for horizontal scaling)</li>
 * <li>Cooldown logic for request throttling</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 * <li>Used in controllers/services to limit request rate per user.</li>
 * <li>secondsUntilAllowed returns remaining cooldown time.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
@Component
public class RateLimiter {

    private static final Duration COOLDOWN = Duration.ofSeconds(5);

    private final ConcurrentHashMap<String, Instant> lastRequest = new ConcurrentHashMap<>();

    /**
     * Returns true if the request is allowed, false if the cooldown hasn't passed.
     * The check and update are atomic — no race condition between two threads
     * for the same UID.
     */
    public boolean isAllowed(String uid) {
        Instant now = Instant.now();
        AtomicBoolean allowed = new AtomicBoolean(false);

        lastRequest.compute(uid, (key, last) -> {
            if (last == null || now.isAfter(last.plus(COOLDOWN))) {
                allowed.set(true);
                return now; // update the timestamp
            }
            return last; // keep the old timestamp, deny the request
        });

        return allowed.get();
    }

    /** Returns how many seconds remain before the next request is allowed. */
    public long secondsUntilAllowed(String uid) {
        Instant last = lastRequest.get(uid);
        if (last == null)
            return 0;
        long remaining = COOLDOWN.getSeconds() - Duration.between(last, Instant.now()).getSeconds();
        return Math.max(0, remaining);
    }
}