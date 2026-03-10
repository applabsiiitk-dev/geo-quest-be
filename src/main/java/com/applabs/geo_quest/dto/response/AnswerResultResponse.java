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
}