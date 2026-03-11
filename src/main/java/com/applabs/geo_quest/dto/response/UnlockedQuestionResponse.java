package com.applabs.geo_quest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Sent to the Flutter client when questions are unlocked near the user's GPS.
 *
 * If {@code locked} is true, all question fields except {@code locationName}
 * and {@code distanceMeters} will be null/zero — the marker is occupied by
 * two other active sessions. The client should show a "locked" state on the
 * map pin and prompt the team to move on and return later.
 *
 * NOTE: correctAnswer is intentionally excluded — never send it to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnlockedQuestionResponse {
    private String questionId;
    private String title;
    private String description;
    private int difficulty;
    private int points;
    private String category;
    private List<String> options;
    private double distanceMeters;

    /** Human-readable location name, always present (even when locked). */
    private String locationName;

    /**
     * True when every alternate question at this location+tier is already
     * assigned to another active session. The team should come back later.
     */
    @Builder.Default
    private boolean locked = false;
}