package com.applabs.geo_quest.repository;

import com.applabs.geo_quest.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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