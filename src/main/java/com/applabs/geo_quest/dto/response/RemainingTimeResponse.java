package com.applabs.geo_quest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemainingTimeResponse {
    private long remainingSeconds;
    private boolean sessionActive;
}
