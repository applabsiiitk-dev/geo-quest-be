package com.applabs.geo_quest.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

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