package com.applabs.geo_quest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Sent to the Flutter client when questions are unlocked.
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
    private List<String> options; // null for free-text questions
    private double distanceMeters;
}
