package com.applabs.geo_quest.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for the current question to be answered in a session.
 * <p>
 * Includes question details and location coordinates for proximity tracking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentQuestionResponse {
    private String questionId;
    private String title;
    private String description;
    private int difficulty;
    private int points;
    private String category;
    private List<String> options;
    private String locationName;
    
    /** GPS coordinates for proximity checking */
    private double latitude;
    private double longitude;
    private double unlockRadius;
    
    /** Number of questions answered so far */
    private int questionsAnswered;
    
    /** Total questions in the session */
    private int totalQuestions;
    
    /** Whether the user is within range to answer this question */
    private boolean withinRange;
}
