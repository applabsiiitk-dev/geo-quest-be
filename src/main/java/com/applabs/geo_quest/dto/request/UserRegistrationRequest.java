/**
 * Request DTO for user registration in GeoQuest.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>displayName</b>: User's display name.</li>
 *   <li><b>email</b>: User's email address.</li>
 *   <li><b>photoUrl</b>: URL to user's profile photo (optional).</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers to register new users.</li>
 *   <li>Validated for display name and email format.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationRequest {

    @NotBlank(message = "Display name is required")
    private String displayName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    private String email;

    private String photoUrl;
}
