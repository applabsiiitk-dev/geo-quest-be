/**
 * Request DTO for creating a new team in GeoQuest.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>teamName</b>: Name of the team.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers to create a team before starting a session.</li>
 *   <li>Validated for non-blank team name.</li>
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
public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    private String teamName;
}
