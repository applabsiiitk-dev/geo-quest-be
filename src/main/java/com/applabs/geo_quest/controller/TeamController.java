// PLAN: Riddle-based hints implementation
// - Team logic may need to be aware of hint changes for question progress
package com.applabs.geo_quest.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.applabs.geo_quest.dto.request.CreateTeamRequest;
import com.applabs.geo_quest.exception.AccessDeniedException;
import com.applabs.geo_quest.exception.TeamNotFoundException;
import com.applabs.geo_quest.model.Team;
import com.applabs.geo_quest.repository.TeamRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teams")
/**
 * Controller for team management endpoints in GeoQuest.
 * <p>
 * Handles creation, joining, listing, and member management for teams.
 * Delegates persistence
 * to TeamRepository. Enforces team size and membership rules.
 * <p>
 * Endpoints:
 * <ul>
 * <li>GET /api/teams — List all active teams</li>
 * <li>GET /api/teams/{teamId} — Get team details</li>
 * <li>POST /api/teams — Create a new team</li>
 * <li>POST /api/teams/{teamId}/join — Join an existing team</li>
 * <li>DELETE /api/teams/{teamId}/members/{memberId} — Remove or leave a team
 * member</li>
 * </ul>
 * <p>
 * Enforces access control for member removal and team joining.
 *
 * @author fl4nk3r
 */
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
