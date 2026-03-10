package com.applabs.geo_quest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnlockQuestionsRequest {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    private double userLat;
    private double userLng;
}
