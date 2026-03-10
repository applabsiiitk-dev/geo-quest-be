package com.applabs.geo_quest.repository;

import com.applabs.geo_quest.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findByTeamIdAndStatus(String teamId, String status);
    List<Session> findByUid(String uid);
    List<Session> findByStatus(String status);
}