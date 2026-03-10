package com.applabs.geo_quest.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple in-memory rate limiter for the /questions/trigger endpoint.
 * Prevents users from spamming proximity triggers faster than 5 seconds.
 *
 * NOTE: This is per-instance. If you scale horizontally, use Redis instead.
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
                return now;   // update the timestamp
            }
            return last;      // keep the old timestamp, deny the request
        });

        return allowed.get();
    }

    /** Returns how many seconds remain before the next request is allowed. */
    public long secondsUntilAllowed(String uid) {
        Instant last = lastRequest.get(uid);
        if (last == null) return 0;
        long remaining = COOLDOWN.getSeconds() - Duration.between(last, Instant.now()).getSeconds();
        return Math.max(0, remaining);
    }
}