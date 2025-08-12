package com.example.weatherviewer.repository;

import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findByIdAndExpiresAtAfter(UUID id, LocalDateTime now);

    Optional<Session> findByUser(User user);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.expiresAt < :now")
    int deleteExpiredSessions(@Param("now") LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
