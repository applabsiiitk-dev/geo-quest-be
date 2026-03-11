/**
 * Represents a GeoQuest user (player), typically authenticated via Google.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>uid</b>: Unique user ID (Google sub, not auto-generated).</li>
 *   <li><b>email</b>: User's email address.</li>
 *   <li><b>displayName</b>: User's display name.</li>
 *   <li><b>photoUrl</b>: URL to the user's profile photo.</li>
 *   <li><b>createdAt</b>: Timestamp when the user was created in the system.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Users are registered and authenticated before joining teams or sessions.</li>
 *   <li>User info is used for access control, display, and progress tracking.</li>
 * </ul>
 * <p>
 * Entity mapping:
 * <ul>
 *   <li>Mapped to the "users" table.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    private String uid; // Google sub — not auto-generated

    private String email;
    private String displayName;
    private String photoUrl;
    private Instant createdAt;
}