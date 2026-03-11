/**
 * Request DTO for starting a new session in GeoQuest.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>teamId</b>: Team identifier.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers to start a session for a team.</li>
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
public class StartSessionRequest {

    @NotBlank(message = "Team ID is required")
    private String teamId;
}
