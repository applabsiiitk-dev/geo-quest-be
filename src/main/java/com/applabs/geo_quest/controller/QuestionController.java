package com.applabs.geo_quest.controller;

import com.applabs.geo_quest.dto.request.CreateQuestionRequest;
import com.applabs.geo_quest.dto.request.UnlockQuestionsRequest;
import com.applabs.geo_quest.dto.response.UnlockedQuestionResponse;
import com.applabs.geo_quest.exception.AccessDeniedException;
import com.applabs.geo_quest.exception.QuestionNotFoundException;
import com.applabs.geo_quest.exception.SessionNotFoundException;
import com.applabs.geo_quest.model.Question;
import com.applabs.geo_quest.model.Session;
import com.applabs.geo_quest.model.Team;
import com.applabs.geo_quest.repository.QuestionRepository;
import com.applabs.geo_quest.repository.SessionRepository;
import com.applabs.geo_quest.repository.TeamRepository;
import com.applabs.geo_quest.security.RateLimiter;
import com.applabs.geo_quest.service.LocationService;
import com.applabs.geo_quest.service.SessionTimerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final SessionRepository sessionRepository;
    private final TeamRepository teamRepository;
    private final LocationService locationService;
    private final SessionTimerService sessionTimerService;
    private final RateLimiter rateLimiter;

    @Autowired
    public QuestionController(QuestionRepository questionRepository,
                               SessionRepository sessionRepository,
                               TeamRepository teamRepository,
                               LocationService locationService,
                               SessionTimerService sessionTimerService,
                               RateLimiter rateLimiter) {
        this.questionRepository = questionRepository;
        this.sessionRepository = sessionRepository;
        this.teamRepository = teamRepository;
        this.locationService = locationService;
        this.sessionTimerService = sessionTimerService;
        this.rateLimiter = rateLimiter;
    }

    /**
     * GET /api/questions/{questionId}
     * Returns a single question — WITHOUT the correctAnswer field.
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<UnlockedQuestionResponse> getQuestion(@PathVariable String questionId) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        UnlockedQuestionResponse response = UnlockedQuestionResponse.builder()
                .questionId(q.getQuestionId())
                .title(q.getTitle())
                .description(q.getDescription())
                .difficulty(q.getDifficulty())
                .points(q.getPoints())
                .category(q.getCategory())
                .options(q.getOptions())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/questions/unlock
     * Returns questions near the user's GPS location, filtered by session difficulty.
     * Rate-limited to 1 request per 5 seconds per user.
     */
    @PostMapping("/unlock")
    public ResponseEntity<?> getUnlockedQuestions(
            @Valid @RequestBody UnlockQuestionsRequest request,
            @AuthenticationPrincipal String uid) {

        // Rate limit check
        if (!rateLimiter.isAllowed(uid)) {
            long wait = rateLimiter.secondsUntilAllowed(uid);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many requests", "retryAfterSeconds", wait));
        }

        // Load session and verify ownership
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException(request.getSessionId()));

        if (sessionTimerService.isSessionExpired(session)) {
            throw new IllegalStateException("Session has expired");
        }

        Team team = teamRepository.findById(session.getTeamId())
                .orElseThrow();

        if (!team.getMembers().contains(uid)) {
            throw new AccessDeniedException("You are not a member of this session's team");
        }

        // Determine difficulty from current score
        int difficulty = sessionTimerService.getDifficultyForScore(session.getScore());

        List<UnlockedQuestionResponse> unlocked = locationService.getUnlockedQuestions(
                request.getUserLat(), request.getUserLng(), difficulty);

        return ResponseEntity.ok(unlocked);
    }

    /**
     * POST /api/questions  [ADMIN ONLY]
     * Creates a new question. correctAnswer is stored securely server-side.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request) {

        Question question = Question.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .difficulty(request.getDifficulty())
                .points(request.getPoints())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .unlockRadius(request.getUnlockRadius())
                .category(request.getCategory())
                .correctAnswer(request.getCorrectAnswer())
                .options(request.getOptions())
                .createdAt(Instant.now())
                .build();

        Question saved = questionRepository.save(question);
        return ResponseEntity.ok(Map.of("questionId", saved.getQuestionId()));
    }

    /**
     * DELETE /api/questions/{questionId}  [ADMIN ONLY]
     */
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new QuestionNotFoundException(questionId);
        }
        questionRepository.deleteById(questionId);
        return ResponseEntity.noContent().build();
    }
}
