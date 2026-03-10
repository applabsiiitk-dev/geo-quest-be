package com.applabs.geo_quest.repository;

import com.applabs.geo_quest.model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, String> {
    List<Leaderboard> findAllByOrderByScoreDesc();
}