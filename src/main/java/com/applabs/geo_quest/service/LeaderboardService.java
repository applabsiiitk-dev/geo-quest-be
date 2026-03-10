package com.applabs.geo_quest.service;

import com.applabs.geo_quest.model.Leaderboard;
import com.applabs.geo_quest.repository.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    @Autowired
    public LeaderboardService(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    /** Returns all entries sorted by score descending. */
    public List<Leaderboard> getLeaderboard() {
        return leaderboardRepository.findAllByOrderByScoreDesc();
    }

    /**
     * Upserts the score for a team.
     * Called automatically after every correct answer.
     */
    public void updateScore(String teamId, String teamName, int newScore) {
        Leaderboard entry = leaderboardRepository.findById(teamId)
                .orElseGet(() -> Leaderboard.builder()
                        .teamId(teamId)
                        .teamName(teamName)
                        .build());

        entry.setScore(newScore);
        entry.setLastUpdated(Instant.now());
        leaderboardRepository.save(entry);
    }
}
