package com.applabs.geo_quest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

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
