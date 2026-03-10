package com.applabs.geo_quest.controller;

import com.applabs.geo_quest.dto.request.CreateTeamRequest;
import com.applabs.geo_quest.exception.AccessDeniedException;
import com.applabs.geo_quest.exception.TeamNotFoundException;
import com.applabs.geo_quest.model.Team;
import com.applabs.geo_quest.repository.TeamRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /** GET /api/teams — list all active teams */
    @GetMapping
    public ResponseEntity<List<Team>> getActiveTeams() {
        return ResponseEntity.ok(teamRepository.findByIsActive(true));
    }

    /** GET /api/teams/{teamId} */
    @GetMapping("/{teamId}")
    public ResponseEntity<Team> getTeam(@PathVariable String teamId) {
        return teamRepository.findById(teamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/teams — create a new team */
    @PostMapping
    public ResponseEntity<Team> createTeam(
            @Valid @RequestBody CreateTeamRequest request,
            @AuthenticationPrincipal String uid) {

        List<String> members = new ArrayList<>();
        members.add(uid); // creator is the first member

        Team team = Team.builder()
                .teamName(request.getTeamName())
                .members(members)
                .createdBy(uid)
                .createdAt(Instant.now())
                .isActive(true)
                .build();

        return ResponseEntity.ok(teamRepository.save(team));
    }

    /** POST /api/teams/{teamId}/join — join an existing team */
    @PostMapping("/{teamId}/join")
    public ResponseEntity<Team> joinTeam(
            @PathVariable String teamId,
            @AuthenticationPrincipal String uid) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        if (!team.isActive()) {
            throw new IllegalStateException("This team is no longer active");
        }
        if (team.getMembers().size() >= 4) {
            throw new IllegalStateException("Team is full (max 4 members)");
        }
        if (!team.getMembers().contains(uid)) {
            team.getMembers().add(uid);
            teamRepository.save(team);
        }

        return ResponseEntity.ok(team);
    }

    /** DELETE /api/teams/{teamId}/members/{memberId} — leave or remove a member */
    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable String teamId,
            @PathVariable String memberId,
            @AuthenticationPrincipal String uid) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        // Only the target member themselves or the team creator can remove
        if (!uid.equals(memberId) && !uid.equals(team.getCreatedBy())) {
            throw new AccessDeniedException("You cannot remove this member");
        }

        team.getMembers().remove(memberId);
        teamRepository.save(team);
        return ResponseEntity.noContent().build();
    }
}
