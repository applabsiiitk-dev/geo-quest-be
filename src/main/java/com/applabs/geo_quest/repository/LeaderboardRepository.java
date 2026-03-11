/**
 * Repository interface for GeoQuest Leaderboard entities.
 * <p>
 * Provides CRUD operations and custom queries for leaderboard management.
 * <p>
 * Methods:
 * <ul>
 *   <li><b>findAllByOrderByScoreDesc</b>: Finds all leaderboard entries ordered by score descending.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used for updating and retrieving team rankings.</li>
 *   <li>Extends JpaRepository for standard CRUD operations.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.applabs.geo_quest.model.Leaderboard;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, String> {
    List<Leaderboard> findAllByOrderByScoreDesc();
}