/**
 * Repository interface for GeoQuest Session entities.
 * <p>
 * Provides CRUD operations and custom queries for session management.
 * <p>
 * Methods:
 * <ul>
 *   <li><b>findByTeamIdAndStatus</b>: Finds a session by team ID and status.</li>
 *   <li><b>findByUid</b>: Finds sessions by user ID.</li>
 *   <li><b>findByStatus</b>: Finds sessions by status.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used for session creation, progress tracking, and status management.</li>
 *   <li>Extends JpaRepository for standard CRUD operations.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.applabs.geo_quest.enums.SessionStatus;
import com.applabs.geo_quest.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findByTeamIdAndStatus(String teamId, SessionStatus status);

    List<Session> findByUid(String uid);

    List<Session> findByStatus(SessionStatus status);
}