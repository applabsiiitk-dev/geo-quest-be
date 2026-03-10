package com.applabs.geo_quest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerSubmissionRequest {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotBlank(message = "Question ID is required")
    private String questionId;

    @NotBlank(message = "Answer is required")
    private String answer;

    /**
     * The spawn point this question came from.
     * Required for per-team, per-marker cooldown tracking —
     * without this the server cannot know which marker to lock
     * after a correct answer.
     */
    @NotBlank(message = "Spawn location ID is required")
    private String spawnLocationId;
}