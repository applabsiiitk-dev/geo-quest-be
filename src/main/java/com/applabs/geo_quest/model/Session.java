/**
 * Represents a GeoQuest game session for a team.
 * <p>
 * Each session tracks the team's progress, assigned questions, score, and state.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>sessionId</b>: Unique identifier (UUID) for the session.</li>
 *   <li><b>teamId</b>: ID of the team playing this session.</li>
 *   <li><b>uid</b>: User ID of the session creator or leader.</li>
 *   <li><b>startTime</b>: Timestamp when the session started.</li>
 *   <li><b>endTime</b>: Timestamp when the session ended.</li>
 *   <li><b>score</b>: Current score for the session.</li>
 *   <li><b>status</b>: Session status (ACTIVE, COMPLETED, etc.).</li>
 *   <li><b>answeredQuestionIds</b>: List of question IDs answered correctly by the team.</li>
 *   <li><b>assignedQuestionIds</b>: List of question IDs assigned to this session (one per location+tier).</li>
 *   <li><b>markerCooldowns</b>: Map of spawn location IDs to cooldown expiry epochs (per-marker cooldowns).</li>
 *   <li><b>questionAttempts</b>: Map of question IDs to number of wrong attempts (max 2 before lock).</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Sessions are created when a team starts a game.</li>
 *   <li>Tracks which questions are assigned and answered for progress and scoring.</li>
 *   <li>Enforces cooldowns and attempt limits for fairness.</li>
 *   <li>Used to determine which riddle/hint to send after each correct answer.</li>
 * </ul>
 * <p>
 * Entity mapping:
 * <ul>
 *   <li>Mapped to the "sessions" table.</li>
 *   <li>Collections and maps stored in separate tables for efficient querying.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.applabs.geo_quest.enums.SessionStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
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
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sessionId;

    private String teamId;
    private String uid;
    private Instant startTime;
    private Instant endTime;

    @Builder.Default
    private int score = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    @ElementCollection
    @CollectionTable(name = "session_answered_questions", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "question_id")
    @Builder.Default
    private List<String> answeredQuestionIds = new ArrayList<>();

    /**
     * The one question per location+tier assigned to this session.
     * Built at session-start by shuffling the question pool and picking
     * the first question per locationName per difficulty tier.
     * LocationService filters to only these IDs so the team never sees
     * the alternate questions assigned to other sessions.
     */
    @ElementCollection
    @CollectionTable(name = "session_assigned_questions", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "question_id")
    @Builder.Default
    private List<String> assignedQuestionIds = new ArrayList<>();

    // Per-marker cooldowns: spawnLocationId → epoch second when cooldown expires
    @ElementCollection
    @CollectionTable(name = "session_marker_cooldowns", joinColumns = @JoinColumn(name = "session_id"))
    @MapKeyColumn(name = "spawn_location_id")
    @Column(name = "cooldown_until_epoch")
    @Builder.Default
    private Map<String, Long> markerCooldowns = new HashMap<>();

    // Wrong attempt counter: questionId → number of wrong attempts (max 2 before
    // lock)
    @ElementCollection
    @CollectionTable(name = "session_question_attempts", joinColumns = @JoinColumn(name = "session_id"))
    @MapKeyColumn(name = "question_id")
    @Column(name = "wrong_attempts")
    @Builder.Default
    private Map<String, Integer> questionAttempts = new HashMap<>();

    // Randomized question trail for this session
    @ElementCollection
    @CollectionTable(name = "session_question_trail", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "question_id")
    @Builder.Default
    private List<String> questionTrail = new ArrayList<>();

    // Current index in the question trail
    @Builder.Default
    private int currentTrailIndex = 0;
}