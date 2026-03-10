package com.applabs.geo_quest.repository;

import com.applabs.geo_quest.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {

    List<Team> findByIsActive(boolean isActive);

    @Query("SELECT t FROM Team t JOIN t.members m WHERE m = :uid AND t.isActive = true")
    List<Team> findActiveTeamsByMemberUid(@Param("uid") String uid);
}