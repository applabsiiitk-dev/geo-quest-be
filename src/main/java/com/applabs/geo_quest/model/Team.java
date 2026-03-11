/**
 * Represents a GeoQuest team, which is a group of players participating together.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>teamId</b>: Unique identifier (UUID) for the team.</li>
 *   <li><b>teamName</b>: Display name of the team.</li>
 *   <li><b>members</b>: List of user IDs for team members.</li>
 *   <li><b>createdBy</b>: User ID of the team creator.</li>
 *   <li><b>createdAt</b>: Timestamp when the team was created.</li>
 *   <li><b>isActive</b>: Whether the team is currently active in a session.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Teams are created before starting a session.</li>
 *   <li>Team membership is used for access control and progress tracking.</li>
 *   <li>Active teams are shown on the leaderboard and can participate in sessions.</li>
 * </ul>
 * <p>
 * Entity mapping:
 * <ul>
 *   <li>Mapped to the "teams" table.</li>
 *   <li>Members stored in a separate collection table for efficient querying.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String teamId;

    private String teamName;

    @ElementCollection
    @CollectionTable(name = "team_members", joinColumns = @JoinColumn(name = "team_id"))
    @Column(name = "member_uid")
    @Builder.Default
    private List<String> members = new ArrayList<>();

    private String createdBy;
    private Instant createdAt;
    private boolean isActive;
}