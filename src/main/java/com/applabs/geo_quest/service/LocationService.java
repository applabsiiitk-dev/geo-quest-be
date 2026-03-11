/**
 * Service for managing question location logic in GeoQuest.
 * <p>
 * Handles GPS proximity, question unlocking, and distance calculations.
 * <p>
 * Methods:
 * <ul>
 *   <li><b>getUnlockedQuestions</b>: Returns questions near user's GPS location.</li>
 *   <li><b>distanceBetween</b>: Calculates Haversine distance between two points.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers/services to filter and unlock questions for sessions.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.applabs.geo_quest.dto.response.UnlockedQuestionResponse;
import com.applabs.geo_quest.model.Question;
import com.applabs.geo_quest.repository.QuestionRepository;

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
         * Returns one response per location within GPS range, filtered by difficulty.
         *
         * For each location in range:
         * - If one of the two alternates is in this session's assignedQuestionIds
         * → return that question (normal unlocked response).
         * - If NEITHER alternate is assigned to this session (both taken by other
         * active sessions) → return a locked marker so the client can show the
         * "occupied" state and prompt the team to move on.
         *
         * @param userLat             User's latitude
         * @param userLng             User's longitude
         * @param difficulty          Current difficulty level (1, 2, or 3)
         * @param assignedQuestionIds This session's assigned question IDs
         * @param allActiveAssigned   All question IDs currently assigned to ANY
         *                            active session (used to detect fully-taken slots)
         */
        public List<UnlockedQuestionResponse> getUnlockedQuestions(
                        double userLat, double userLng,
                        int difficulty,
                        List<String> assignedQuestionIds,
                        Set<String> allActiveAssigned) {

                double maxRadius = 500.0;
                double deltaLat = maxRadius / METERS_PER_DEGREE_LAT;
                double deltaLng = maxRadius / (METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(userLat)));

                List<Question> candidates = questionRepository.findQuestionsInBoundingBox(
                                userLat - deltaLat, userLat + deltaLat,
                                userLng - deltaLng, userLng + deltaLng);

                // Group nearby questions by locationName, filtered by difficulty + exact radius
                Map<String, List<Question>> byLocation = candidates.stream()
                                .filter(q -> q.getDifficulty() == difficulty)
                                .filter(q -> distanceBetween(userLat, userLng,
                                                q.getLatitude(), q.getLongitude()) <= q.getUnlockRadius())
                                .collect(Collectors.groupingBy(Question::getLocationName));

                List<UnlockedQuestionResponse> results = new ArrayList<>();

                for (Map.Entry<String, List<Question>> entry : byLocation.entrySet()) {
                        String locationName = entry.getKey();
                        List<Question> alternates = entry.getValue();
                        double dist = distanceBetween(userLat, userLng,
                                        alternates.get(0).getLatitude(), alternates.get(0).getLongitude());

                        // Find the alternate assigned to THIS session
                        Optional<Question> mine = alternates.stream()
                                        .filter(q -> assignedQuestionIds.contains(q.getQuestionId()))
                                        .findFirst();

                        if (mine.isPresent()) {
                                // Happy path — this team has a question here
                                results.add(toResponse(mine.get(), dist));
                        } else {
                                // Check if ALL alternates are taken by other active sessions
                                boolean allTaken = alternates.stream()
                                                .allMatch(q -> allActiveAssigned.contains(q.getQuestionId()));

                                if (allTaken) {
                                        // Every alternate is in use — show a locked marker
                                        results.add(UnlockedQuestionResponse.builder()
                                                        .locationName(locationName)
                                                        .distanceMeters(Math.round(dist * 10.0) / 10.0)
                                                        .locked(true)
                                                        .build());
                                }
                                // If not all taken but none assigned to this session, this location
                                // simply wasn't assigned to this team at session start — skip it.
                        }
                }

                return results;
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
                                .locationName(q.getLocationName())
                                .options(q.getOptions())
                                .distanceMeters(Math.round(distanceMeters * 10.0) / 10.0)
                                .locked(false)
                                .build();
        }
}