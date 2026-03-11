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
    @CollectionTable(name = "session_answered_questions",
                     joinColumns = @JoinColumn(name = "session_id"))
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
    @CollectionTable(name = "session_assigned_questions",
                     joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "question_id")
    @Builder.Default
    private List<String> assignedQuestionIds = new ArrayList<>();

    // Per-marker cooldowns: spawnLocationId → epoch second when cooldown expires
    @ElementCollection
    @CollectionTable(name = "session_marker_cooldowns",
                     joinColumns = @JoinColumn(name = "session_id"))
    @MapKeyColumn(name = "spawn_location_id")
    @Column(name = "cooldown_until_epoch")
    @Builder.Default
    private Map<String, Long> markerCooldowns = new HashMap<>();

    // Wrong attempt counter: questionId → number of wrong attempts (max 2 before lock)
    @ElementCollection
    @CollectionTable(name = "session_question_attempts",
                     joinColumns = @JoinColumn(name = "session_id"))
    @MapKeyColumn(name = "question_id")
    @Column(name = "wrong_attempts")
    @Builder.Default
    private Map<String, Integer> questionAttempts = new HashMap<>();
}