/**
 * Request DTO for creating a new question in GeoQuest.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>title</b>: Question title.</li>
 *   <li><b>description</b>: Riddle/clue for the next location.</li>
 *   <li><b>difficulty</b>: Difficulty tier (1, 2, or 3).</li>
 *   <li><b>points</b>: Points awarded for correct answer.</li>
 *   <li><b>latitude</b>: GPS latitude for the question marker.</li>
 *   <li><b>longitude</b>: GPS longitude for the question marker.</li>
 *   <li><b>unlockRadius</b>: Radius (meters) for answer eligibility.</li>
 *   <li><b>category</b>: Question category.</li>
 *   <li><b>correctAnswer</b>: The correct answer string.</li>
 *   <li><b>options</b>: Optional list of answer options (multiple choice).</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers to create questions for the game.</li>
 *   <li>Validated for required fields and value ranges.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.dto.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateQuestionRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 1, message = "Difficulty must be 1, 2, or 3")
    private int difficulty;

    @Min(value = 1, message = "Points must be positive")
    private int points;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @Min(value = 1, message = "Unlock radius must be positive")
    private double unlockRadius;

    private String category;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    /** Optional: for multiple-choice questions */
    private List<String> options;
}
