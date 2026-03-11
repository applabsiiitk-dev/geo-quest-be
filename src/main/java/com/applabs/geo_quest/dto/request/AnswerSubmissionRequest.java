/**
 * Request DTO for submitting an answer in GeoQuest.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>sessionId</b>: Session identifier.</li>
 *   <li><b>questionId</b>: Question identifier.</li>
 *   <li><b>answer</b>: Submitted answer string.</li>
 *   <li><b>spawnLocationId</b>: Spawn location identifier.</li>
 *   <li><b>userLat</b>: User's latitude.</li>
 *   <li><b>userLng</b>: User's longitude.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers to process answer submissions.</li>
 *   <li>Validated for required fields and value ranges.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerSubmissionRequest {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotBlank(message = "Question ID is required")
    private String questionId;

    @NotBlank(message = "Answer is required")
    private String answer;

    @NotBlank(message = "Spawn location ID is required")
    private String spawnLocationId;

    @NotNull(message = "userLat is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double userLat;

    @NotNull(message = "userLng is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double userLng;
}