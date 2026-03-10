package com.applabs.geo_quest.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Builder.Default
    private String status = "active";

    @ElementCollection
    @CollectionTable(name = "session_answered_questions",
                     joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "question_id")
    @Builder.Default
    private List<String> answeredQuestionIds = new ArrayList<>();

    // Per-marker cooldowns: spawnLocationId → epoch second when cooldown expires
    @ElementCollection
    @CollectionTable(name = "session_marker_cooldowns",
                     joinColumns = @JoinColumn(name = "session_id"))
    @MapKeyColumn(name = "spawn_location_id")
    @Column(name = "cooldown_until_epoch")
    @Builder.Default
    private Map<String, Long> markerCooldowns = new HashMap<>();
}