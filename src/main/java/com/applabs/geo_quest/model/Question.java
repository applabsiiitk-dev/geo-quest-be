package com.applabs.geo_quest.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String questionId;

    private String title;

    @Column(length = 1000)
    private String description;

    private int difficulty;
    private int points;
    private double latitude;
    private double longitude;
    private double unlockRadius;
    private String category;
    private String correctAnswer;

    @ElementCollection
    @CollectionTable(name = "question_options",
                     joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    private List<String> options;

    private Instant createdAt;
}