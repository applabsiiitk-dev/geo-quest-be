/**
 * Represents a leaderboard entry for a GeoQuest team.
 * <p>
 * Tracks the team's score and last update time for ranking purposes.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>teamId</b>: Unique identifier for the team (not auto-generated).</li>
 *   <li><b>teamName</b>: Display name of the team.</li>
 *   <li><b>score</b>: Current score for the team.</li>
 *   <li><b>lastUpdated</b>: Timestamp of the last score update.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Leaderboard entries are updated after each correct answer.</li>
 *   <li>Used to display team rankings and progress.</li>
 * </ul>
 * <p>
 * Entity mapping:
 * <ul>
 *   <li>Mapped to the "leaderboard" table.</li>
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
@Table(name = "leaderboard")
public class Leaderboard {

    @Id
    private String teamId; // not auto-generated — set explicitly

    private String teamName;

    @Builder.Default
    private int score = 0;

    private Instant lastUpdated;
}