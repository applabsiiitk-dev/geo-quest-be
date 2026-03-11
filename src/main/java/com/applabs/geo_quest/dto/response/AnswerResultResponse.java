/**
 * Response sent to the client after an answer submission in GeoQuest.
 * <p>
 * Contains result status, scoring, cooldown info, and next location hint.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>correct</b>: Whether the answer was correct.</li>
 *   <li><b>message</b>: Feedback message for the user.</li>
 *   <li><b>pointsAwarded</b>: Points earned for this answer.</li>
 *   <li><b>totalScore</b>: Team's total score after this answer.</li>
 *   <li><b>cooldownUntil</b>: ISO-8601 timestamp for marker cooldown expiry.</li>
 *   <li><b>nextHint</b>: Riddle/clue for the next location (sent only on correct answer).</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers to return answer results to the client.</li>
 *   <li>Flutter client uses cooldownUntil for countdown timer and nextHint for navigation.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerResultResponse {
    private boolean correct;
    private String message;
    private int pointsAwarded;
    private int totalScore;

    /**
     * ISO-8601 timestamp of when this team's cooldown expires for the marker.
     * Null if the answer was wrong (no cooldown set) or if already on cooldown.
     * Flutter should use this to show a countdown timer before the team
     * can return to the same spawn point.
     */
    private String cooldownUntil;

    /**
     * Riddle/clue for the next location, sent only on correct answer.
     */
    private String nextHint;
}