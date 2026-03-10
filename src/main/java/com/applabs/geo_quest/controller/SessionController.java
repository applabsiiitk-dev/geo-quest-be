package com.applabs.geo_quest.controller;

import com.applabs.geo_quest.dto.request.StartSessionRequest;
import com.applabs.geo_quest.dto.response.RemainingTimeResponse;
import com.applabs.geo_quest.exception.AccessDeniedException;
import com.applabs.geo_quest.exception.SessionNotFoundException;
import com.applabs.geo_quest.exception.TeamNotFoundException;
import com.applabs.geo_quest.model.Session;
import com.applabs.geo_quest.model.Team;
import com.applabs.geo_quest.repository.SessionRepository;
import com.applabs.geo_quest.repository.TeamRepository;
import com.applabs.geo_quest.service.SessionTimerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionRepository sessionRepository;
    private final TeamRepository teamRepository;
    private final SessionTimerService sessionTimerService;

    @Autowired
    public SessionController(SessionRepository sessionRepository,
                              TeamRepository teamRepository,
                              SessionTimerService sessionTimerService) {
        this.sessionRepository = sessionRepository;
        this.teamRepository = teamRepository;
        this.sessionTimerService = sessionTimerService;
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
        sessionRepository.findByTeamIdAndStatus(request.getTeamId(), "active")
                .ifPresent(s -> { throw new IllegalStateException("Team already has an active session"); });

        Instant start = Instant.now();
        Session session = Session.builder()
                .teamId(request.getTeamId())
                .uid(uid)
                .startTime(start)
                .endTime(sessionTimerService.computeEndTime(start))
                .score(0)
                .status("active")
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
        if ("active".equals(session.getStatus()) && sessionTimerService.isSessionExpired(session)) {
            session.setStatus("completed");
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
        boolean active = "active".equals(session.getStatus()) && remaining > 0;

        return ResponseEntity.ok(new RemainingTimeResponse(remaining, active));
    }
}
