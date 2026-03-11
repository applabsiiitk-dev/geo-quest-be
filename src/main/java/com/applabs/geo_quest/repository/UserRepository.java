/**
 * Repository interface for GeoQuest User entities.
 * <p>
 * Provides CRUD operations and custom queries for user management.
 * <p>
 * Methods:
 * <ul>
 *   <li><b>findByEmail</b>: Finds a user by email address.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used for user registration, authentication, and lookup.</li>
 *   <li>Extends JpaRepository for standard CRUD operations.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.applabs.geo_quest.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}