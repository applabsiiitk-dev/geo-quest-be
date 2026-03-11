/**
 * Repository interface for GeoQuest Question entities.
 * <p>
 * Provides CRUD operations and custom queries for question management.
 * <p>
 * Methods:
 * <ul>
 *   <li><b>findByDifficulty</b>: Finds questions by difficulty tier.</li>
 *   <li><b>findByCategory</b>: Finds questions by category.</li>
 *   <li><b>findQuestionsInBoundingBox</b>: Finds questions within a GPS bounding box.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used for seeding, assigning, and querying questions for sessions.</li>
 *   <li>Extends JpaRepository for standard CRUD operations.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.applabs.geo_quest.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

       List<Question> findByDifficulty(int difficulty);

       List<Question> findByCategory(String category);

       @Query("SELECT q FROM Question q WHERE " +
                     "q.latitude  >= :minLat AND q.latitude  <= :maxLat AND " +
                     "q.longitude >= :minLng AND q.longitude <= :maxLng")
       List<Question> findQuestionsInBoundingBox(
                     @Param("minLat") double minLat, @Param("maxLat") double maxLat,
                     @Param("minLng") double minLng, @Param("maxLng") double maxLng);
}