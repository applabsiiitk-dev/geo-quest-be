/**
 * Request DTO for unlocking questions near user's GPS in GeoQuest.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>sessionId</b>: Session identifier.</li>
 *   <li><b>userLat</b>: User's latitude.</li>
 *   <li><b>userLng</b>: User's longitude.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers to unlock questions based on location.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
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
