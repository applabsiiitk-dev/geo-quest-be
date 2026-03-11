/**
 * Repository interface for GeoQuest Team entities.
 * <p>
 * Provides CRUD operations and custom queries for team management.
 * <p>
 * Methods:
 * <ul>
 *   <li><b>findByIsActive</b>: Finds teams by active status.</li>
 *   <li><b>findActiveTeamsByMemberUid</b>: Finds active teams by member UID.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used for team creation, membership management, and session assignment.</li>
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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.applabs.geo_quest.model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {

    List<Team> findByIsActive(boolean isActive);

    @Query("SELECT t FROM Team t JOIN t.members m WHERE m = :uid AND t.isActive = true")
    List<Team> findActiveTeamsByMemberUid(@Param("uid") String uid);
}