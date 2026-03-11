package com.applabs.geo_quest.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response sent to the client when questions are unlocked near the user's GPS
 * in GeoQuest.
 * <p>
 * Contains question details, location info, and lock state.
 * <p>
 * Fields:
 * <ul>
 * <li><b>questionId</b>: Unique identifier for the question.</li>
 * <li><b>title</b>: Question title or prompt.</li>
 * <li><b>description</b>: Riddle/clue for the next location.</li>
 * <li><b>difficulty</b>: Difficulty tier.</li>
 * <li><b>points</b>: Points awarded for correct answer.</li>
 * <li><b>category</b>: Question category.</li>
 * <li><b>options</b>: List of answer options.</li>
 * <li><b>distanceMeters</b>: Distance from user's location to marker.</li>
 * <li><b>locationName</b>: Human-readable campus location name.</li>
 * <li><b>locked</b>: True if all alternates at this location+tier are occupied
 * by other sessions.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 * <li>Used by controllers to return unlocked questions to the client.</li>
 * <li>Flutter client shows locked state if locked=true, omits correctAnswer for
 * security.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
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