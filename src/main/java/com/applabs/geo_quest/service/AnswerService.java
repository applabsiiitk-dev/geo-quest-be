package com.applabs.geo_quest.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.applabs.geo_quest.dto.request.AnswerSubmissionRequest;
import com.applabs.geo_quest.dto.response.AnswerResultResponse;
import com.applabs.geo_quest.exception.AccessDeniedException;
import com.applabs.geo_quest.exception.QuestionNotFoundException;
import com.applabs.geo_quest.exception.SessionNotFoundException;
import com.applabs.geo_quest.model.Question;
import com.applabs.geo_quest.model.Session;
import com.applabs.geo_quest.model.Team;
import com.applabs.geo_quest.repository.QuestionRepository;
import com.applabs.geo_quest.repository.SessionRepository;
import com.applabs.geo_quest.repository.TeamRepository;

@Service
public class AnswerService {

    private static final long MARKER_COOLDOWN_MINUTES = 10;

    private final SessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final TeamRepository teamRepository;
    private final SessionTimerService sessionTimerService;
    private final LeaderboardService leaderboardService;

    @Autowired
    public AnswerService(
            SessionRepository sessionRepository,
            QuestionRepository questionRepository,
            TeamRepository teamRepository,
            SessionTimerService sessionTimerService,
            LeaderboardService leaderboardService) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.teamRepository = teamRepository;
        this.sessionTimerService = sessionTimerService;
        this.leaderboardService = leaderboardService;
    }

    /**
     * Validates and scores an answer submission.
     *
     * @param request The answer submission (includes spawnLocationId for cooldown tracking)
     * @param uid     Google UID of the authenticated user (from JWT)
     * @return Result including correctness, points, updated score and cooldown timestamp
     */
    @Transactional
    public AnswerResultResponse submitAnswer(AnswerSubmissionRequest request, String uid) {

        // 1. Load session
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException(request.getSessionId()));

        // 2. Verify session is active
        if (!"active".equals(session.getStatus())) {
            throw new IllegalStateException("Session is already completed");
        }

        // 3. Verify session hasn't timed out
        if (sessionTimerService.isSessionExpired(session)) {
            session.setStatus("completed");
            sessionRepository.save(session);
            throw new IllegalStateException("Session has expired");
        }

        // 4. Per-marker cooldown check — prevents camping and following other teams
        String spawnId = request.getSpawnLocationId();
        Long cooldownUntilEpoch = session.getMarkerCooldowns().get(spawnId);
        if (cooldownUntilEpoch != null && Instant.now().getEpochSecond() < cooldownUntilEpoch) {
            Instant cooldownUntil = Instant.ofEpochSecond(cooldownUntilEpoch);
            return new AnswerResultResponse(
                false,
                "Marker is on cooldown for your team",
                0,
                session.getScore(),
                cooldownUntil.toString()
            );
        }

        // 5. Duplicate answer check
        if (session.getAnsweredQuestionIds().contains(request.getQuestionId())) {
            return new AnswerResultResponse(
                false,
                "Already answered correctly",
                0,
                session.getScore(),
                null
            );
        }

        // 6. Verify the requesting user belongs to this session's team
        Team team = teamRepository.findById(session.getTeamId())
                .orElseThrow(() -> new IllegalStateException("Team not found for session"));

        if (!team.getMembers().contains(uid)) {
            throw new AccessDeniedException("You are not a member of this team");
        }

        // 7. Load question
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(request.getQuestionId()));

        // 8. Check answer (case-insensitive, trimmed)
        boolean correct = question.getCorrectAnswer() != null &&
                question.getCorrectAnswer().trim().equalsIgnoreCase(request.getAnswer().trim());

        int pointsAwarded = 0;
        String cooldownUntilStr = null;

        if (correct) {
            // 9. Award points
            pointsAwarded = question.getPoints();
            session.setScore(session.getScore() + pointsAwarded);
            session.getAnsweredQuestionIds().add(request.getQuestionId());

            // 10. Set per-marker cooldown so this team must wait before returning
            long cooldownEpoch = Instant.now()
                    .plusSeconds(MARKER_COOLDOWN_MINUTES * 60)
                    .getEpochSecond();
            session.getMarkerCooldowns().put(spawnId, cooldownEpoch);
            cooldownUntilStr = Instant.ofEpochSecond(cooldownEpoch).toString();

            sessionRepository.save(session);

            // 11. Update leaderboard
            leaderboardService.updateScore(session.getTeamId(), team.getTeamName(), session.getScore());
        }

        return new AnswerResultResponse(
                correct,
                correct ? "Correct! +" + pointsAwarded + " points" : "Wrong answer, try again",
                pointsAwarded,
                session.getScore(),
                cooldownUntilStr
        );
    }
}