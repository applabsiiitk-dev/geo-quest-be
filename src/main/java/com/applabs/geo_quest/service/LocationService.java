package com.applabs.geo_quest.service;

import com.applabs.geo_quest.dto.response.UnlockedQuestionResponse;
import com.applabs.geo_quest.model.Question;
import com.applabs.geo_quest.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private static final int EARTH_RADIUS_METERS = 6_371_000;

    /**
     * Degrees of latitude per metre — used to build a bounding box
     * before the exact Haversine check, reducing DB results.
     */
    private static final double METERS_PER_DEGREE_LAT = 111_320.0;

    private final QuestionRepository questionRepository;

    @Autowired
    public LocationService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Returns questions unlocked for the user's current GPS position,
     * filtered by the session's current difficulty level.
     *
     * @param userLat    User's latitude
     * @param userLng    User's longitude
     * @param difficulty Current difficulty level (1, 2, or 3)
     * @return List of unlocked question responses (correctAnswer excluded)
     */
    public List<UnlockedQuestionResponse> getUnlockedQuestions(double userLat, double userLng, int difficulty) {

        // Max unlock radius we'd ever use — used to compute bounding box
        double maxRadius = 500.0;

        double deltaLat = maxRadius / METERS_PER_DEGREE_LAT;
        double deltaLng = maxRadius / (METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(userLat)));

        // Pre-filter in Postgres with bounding box, then apply exact Haversine distance + difficulty
        List<Question> candidates = questionRepository.findQuestionsInBoundingBox(
                userLat - deltaLat, userLat + deltaLat,
                userLng - deltaLng, userLng + deltaLng
        );

        return candidates.stream()
                .filter(q -> q.getDifficulty() == difficulty)
                .map(q -> {
                    double dist = distanceBetween(userLat, userLng, q.getLatitude(), q.getLongitude());
                    return dist <= q.getUnlockRadius() ? toResponse(q, dist) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Haversine formula — accurate great-circle distance in metres.
     */
    public double distanceBetween(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_METERS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private UnlockedQuestionResponse toResponse(Question q, double distanceMeters) {
        return UnlockedQuestionResponse.builder()
                .questionId(q.getQuestionId())
                .title(q.getTitle())
                .description(q.getDescription())
                .difficulty(q.getDifficulty())
                .points(q.getPoints())
                .category(q.getCategory())
                .options(q.getOptions())
                .distanceMeters(Math.round(distanceMeters * 10.0) / 10.0)
                .build();
    }
}