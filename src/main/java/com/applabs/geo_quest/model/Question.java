/**
 * Represents a single GeoQuest question assigned to a campus location.
 * <p>
 * Each question contains a riddle or clue (description) pointing to the next location,
 * a title, answer options, and metadata for scoring and spatial placement.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>questionId</b>: Unique identifier (UUID) for the question.</li>
 *   <li><b>title</b>: Short title or prompt for the question.</li>
 *   <li><b>description</b>: Riddle/clue for the next location (used as a hint).</li>
 *   <li><b>difficulty</b>: Difficulty tier (1=easy, 2=medium, 3=hard).</li>
 *   <li><b>points</b>: Points awarded for correct answer.</li>
 *   <li><b>latitude, longitude</b>: GPS coordinates for the question marker.</li>
 *   <li><b>unlockRadius</b>: Radius (meters) within which the question can be answered.</li>
 *   <li><b>category</b>: Category or theme of the question.</li>
 *   <li><b>locationName</b>: Human-readable campus location name.</li>
 *   <li><b>correctAnswer</b>: The correct answer string.</li>
 *   <li><b>options</b>: List of answer options (multiple choice).</li>
 *   <li><b>createdAt</b>: Timestamp when the question was created.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Questions are seeded at game start and assigned to sessions.</li>
 *   <li>Players must be within unlockRadius to answer.</li>
 *   <li>On correct answer, the description of the next question is sent as a riddle/hint.</li>
 * </ul>
 * <p>
 * Entity mapping:
 * <ul>
 *   <li>Mapped to the "questions" table with indexes for spatial and difficulty queries.</li>
 *   <li>Options stored in a separate collection table.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.model;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "questions", indexes = {
        @Index(name = "idx_question_coords", columnList = "latitude, longitude"),
        @Index(name = "idx_question_difficulty", columnList = "difficulty")
})
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

    /** Human-readable campus location name shown to players on the map. */
    private String locationName;

    private String correctAnswer;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    private List<String> options;

    private Instant createdAt;
}