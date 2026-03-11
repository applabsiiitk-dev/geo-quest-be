/**
 * Service for managing session timing and difficulty in GeoQuest.
 * <p>
 * Handles session expiration, duration, and difficulty mapping.
 * <p>
 * Methods:
 * <ul>
 *   <li><b>computeEndTime</b>: Computes session end time.</li>
 *   <li><b>isSessionExpired</b>: Checks if session is expired.</li>
 *   <li><b>getRemainingSeconds</b>: Gets remaining seconds in session.</li>
 *   <li><b>getDifficultyForScore</b>: Maps score to difficulty tier.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers/services to enforce session rules and filter questions.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.applabs.geo_quest.model.Session;

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
        if (score < 100)
            return 1;
        if (score < 250)
            return 2;
        return 3;
    }
}
