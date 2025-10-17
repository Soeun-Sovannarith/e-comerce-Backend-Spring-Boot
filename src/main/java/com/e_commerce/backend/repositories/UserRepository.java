package com.e_commerce.backend.repositories;

import com.e_commerce.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts, u.lastFailedLogin = :lastFailed WHERE u.username = :username")
    void updateFailedAttempts(String username, int attempts, LocalDateTime lastFailed);

    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = :lockedUntil WHERE u.username = :username")
    void lockUser(String username, LocalDateTime lockedUntil);
}
