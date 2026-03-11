// package com.applabs.geo_quest.controller;

// import java.time.Instant;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.applabs.geo_quest.dto.request.StartSessionRequest;
// import com.applabs.geo_quest.dto.response.RemainingTimeResponse;
// import com.applabs.geo_quest.enums.SessionStatus;
// import com.applabs.geo_quest.exception.AccessDeniedException;
// import com.applabs.geo_quest.exception.SessionNotFoundException;
// import com.applabs.geo_quest.exception.TeamNotFoundException;
// import com.applabs.geo_quest.model.Session;
// import com.applabs.geo_quest.model.Team;
// import com.applabs.geo_quest.repository.SessionRepository;
// import com.applabs.geo_quest.repository.TeamRepository;
// import com.applabs.geo_quest.service.SessionTimerService;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/api/sessions")
// public class SessionController {

//     private final SessionRepository sessionRepository;
//     private final TeamRepository teamRepository;
//     private final SessionTimerService sessionTimerService;

//     @Autowired
//     public SessionController(SessionRepository sessionRepository,
//             TeamRepository teamRepository,
//             SessionTimerService sessionTimerService) {
//         this.sessionRepository = sessionRepository;
//         this.teamRepository = teamRepository;
//         this.sessionTimerService = sessionTimerService;
//     }

//     /**
//      * POST /api/sessions/start
//      * Starts a 2-hour competition session for a team.
//      * Only one active session per team is allowed.
//      */
//     @PostMapping("/start")
//     public ResponseEntity<Session> startSession(
//             @Valid @RequestBody StartSessionRequest request,
//             @AuthenticationPrincipal String uid) {

//         Team team = teamRepository.findById(request.getTeamId())
//                 .orElseThrow(() -> new TeamNotFoundException(request.getTeamId()));

//         // Only team members can start a session
//         if (!team.getMembers().contains(uid)) {
//             throw new AccessDeniedException("You are not a member of this team");
//         }

//         // Prevent duplicate active sessions
//         sessionRepository.findByTeamIdAndStatus(request.getTeamId(), SessionStatus.ACTIVE)
//                 .ifPresent(s -> {
//                     throw new IllegalStateException("Team already has an active session");
//                 });

//         Instant start = Instant.now();
//         Session session = Session.builder()
//                 .teamId(request.getTeamId())
//                 .uid(uid)
//                 .startTime(start)
//                 .endTime(sessionTimerService.computeEndTime(start))
//                 .score(0)
//                 .status(SessionStatus.ACTIVE)
//                 .build();

//         return ResponseEntity.ok(sessionRepository.save(session));
//     }

//     /**
//      * GET /api/sessions/{sessionId}
//      * Returns session details. Only team members can view it.
//      */
//     @GetMapping("/{sessionId}")
//     public ResponseEntity<Session> getSession(
//             @PathVariable String sessionId,
//             @AuthenticationPrincipal String uid) {

//         Session session = sessionRepository.findById(sessionId)
//                 .orElseThrow(() -> new SessionNotFoundException(sessionId));

//         Team team = teamRepository.findById(session.getTeamId())
//                 .orElseThrow(() -> new TeamNotFoundException(session.getTeamId()));

//         if (!team.getMembers().contains(uid)) {
//             throw new AccessDeniedException("You are not a member of this session's team");
//         }

//         // Auto-complete expired sessions on read
//         if (SessionStatus.ACTIVE.equals(session.getStatus()) && sessionTimerService.isSessionExpired(session)) {
//             session.setStatus(SessionStatus.COMPLETED);
//             sessionRepository.save(session);
//         }

//         return ResponseEntity.ok(session);
//     }

//     /**
//      * GET /api/sessions/{sessionId}/remaining-time
//      * Returns remaining seconds and whether the session is still active.
//      */
//     @GetMapping("/{sessionId}/remaining-time")
//     public ResponseEntity<RemainingTimeResponse> getRemainingTime(
//             @PathVariable String sessionId,
//             @AuthenticationPrincipal String uid) {

//         Session session = sessionRepository.findById(sessionId)
//                 .orElseThrow(() -> new SessionNotFoundException(sessionId));

//         long remaining = sessionTimerService.getRemainingSeconds(session);
//         boolean active = SessionStatus.ACTIVE.equals(session.getStatus()) && remaining > 0;

//         return ResponseEntity.ok(new RemainingTimeResponse(remaining, active));
//     }
// }
package com.applabs.geo_quest.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.applabs.geo_quest.dto.request.StartSessionRequest;
import com.applabs.geo_quest.dto.response.RemainingTimeResponse;
import com.applabs.geo_quest.enums.SessionStatus;
import com.applabs.geo_quest.exception.AccessDeniedException;
import com.applabs.geo_quest.exception.SessionNotFoundException;
import com.applabs.geo_quest.exception.TeamNotFoundException;
import com.applabs.geo_quest.model.Session;
import com.applabs.geo_quest.model.Team;
import com.applabs.geo_quest.repository.QuestionRepository;
import com.applabs.geo_quest.repository.SessionRepository;
import com.applabs.geo_quest.repository.TeamRepository;
import com.applabs.geo_quest.service.SessionTimerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionRepository sessionRepository;
    private final TeamRepository teamRepository;
    private final SessionTimerService sessionTimerService;
    private final QuestionRepository questionRepository;

    @Autowired
    public SessionController(SessionRepository sessionRepository,
            TeamRepository teamRepository,
            SessionTimerService sessionTimerService,
            QuestionRepository questionRepository) {
        this.sessionRepository = sessionRepository;
        this.teamRepository = teamRepository;
        this.sessionTimerService = sessionTimerService;
        this.questionRepository = questionRepository;
    }

    /**
     * POST /api/sessions/start
     * Starts a 2-hour competition session for a team.
     * Only one active session per team is allowed.
     */
    @PostMapping("/start")
    public ResponseEntity<Session> startSession(
            @Valid @RequestBody StartSessionRequest request,
            @AuthenticationPrincipal String uid) {

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new TeamNotFoundException(request.getTeamId()));

        // Only team members can start a session
        if (!team.getMembers().contains(uid)) {
            throw new AccessDeniedException("You are not a member of this team");
        }

        // Prevent duplicate active sessions
        sessionRepository.findByTeamIdAndStatus(request.getTeamId(), SessionStatus.ACTIVE)
                .ifPresent(s -> {
                    throw new IllegalStateException("Team already has an active session");
                });

        // ── Assign a uniquely-shuffled question list to this session ─────────
        // Each location has 2 questions per difficulty tier. We shuffle within
        // each tier and pick ONE question per location — the first encountered
        // in the shuffled order. Two teams at the same marker get a different
        // question because their shuffles are independent. LocationService then
        // filters to only the questions present in this list, so the alternate
        // is never surfaced to this team.
        List<String> assigned = new ArrayList<>();
        for (int diff = 1; diff <= 3; diff++) {
            List<com.applabs.geo_quest.model.Question> tierQuestions =
                    questionRepository.findByDifficulty(diff);
            Collections.shuffle(tierQuestions);
            java.util.Set<String> seenLocations = new java.util.LinkedHashSet<>();
            for (com.applabs.geo_quest.model.Question q : tierQuestions) {
                if (seenLocations.add(q.getLocationName())) {
                    assigned.add(q.getQuestionId());
                }
            }
        }

        Instant start = Instant.now();
        Session session = Session.builder()
                .teamId(request.getTeamId())
                .uid(uid)
                .startTime(start)
                .endTime(sessionTimerService.computeEndTime(start))
                .score(0)
                .status(SessionStatus.ACTIVE)
                .assignedQuestionIds(assigned)
                .build();

        return ResponseEntity.ok(sessionRepository.save(session));
    }

    /**
     * GET /api/sessions/{sessionId}
     * Returns session details. Only team members can view it.
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<Session> getSession(
            @PathVariable String sessionId,
            @AuthenticationPrincipal String uid) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        Team team = teamRepository.findById(session.getTeamId())
                .orElseThrow(() -> new TeamNotFoundException(session.getTeamId()));

        if (!team.getMembers().contains(uid)) {
            throw new AccessDeniedException("You are not a member of this session's team");
        }

        // Auto-complete expired sessions on read
        if (SessionStatus.ACTIVE.equals(session.getStatus()) && sessionTimerService.isSessionExpired(session)) {
            session.setStatus(SessionStatus.COMPLETED);
            sessionRepository.save(session);
        }

        return ResponseEntity.ok(session);
    }

    /**
     * GET /api/sessions/{sessionId}/remaining-time
     * Returns remaining seconds and whether the session is still active.
     */
    @GetMapping("/{sessionId}/remaining-time")
    public ResponseEntity<RemainingTimeResponse> getRemainingTime(
            @PathVariable String sessionId,
            @AuthenticationPrincipal String uid) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        long remaining = sessionTimerService.getRemainingSeconds(session);
        boolean active = SessionStatus.ACTIVE.equals(session.getStatus()) && remaining > 0;

        return ResponseEntity.ok(new RemainingTimeResponse(remaining, active));
    }
}