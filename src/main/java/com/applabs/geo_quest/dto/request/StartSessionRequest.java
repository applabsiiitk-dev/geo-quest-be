package com.applabs.geo_quest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartSessionRequest {

    @NotBlank(message = "Team ID is required")
    private String teamId;
}
