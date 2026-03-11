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