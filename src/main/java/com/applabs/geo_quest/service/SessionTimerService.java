package com.applabs.geo_quest.service;

import com.applabs.geo_quest.model.Session;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SessionTimerService {

    private static final long SESSION_DURATION_HOURS = 2;

    /**
     * Computes the endTime for a new session (startTime + 2 hours).
     */
    public Instant computeEndTime(Instant startTime) {
        return startTime.plusSeconds(SESSION_DURATION_HOURS * 3600);
    }

    /**
     * Returns true if the session's endTime has passed.
     */
    public boolean isSessionExpired(Session session) {
        return Instant.now().isAfter(session.getEndTime());
    }

    /**
     * Returns remaining seconds — 0 if already expired.
     */
    public long getRemainingSeconds(Session session) {
        long remaining = session.getEndTime().getEpochSecond() - Instant.now().getEpochSecond();
        return Math.max(0, remaining);
    }

    /**
     * Maps the current session score to a difficulty level (1, 2, or 3).
     * Used by LocationService to filter questions.
     */
    public int getDifficultyForScore(int score) {
        if (score < 100) return 1;
        if (score < 250) return 2;
        return 3;
    }
}
