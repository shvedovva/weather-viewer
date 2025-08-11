package com.example.weatherviewer.repository;

import com.example.weatherviewer.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    Session findByIdAndExpiresAtAfter(UUID id, LocalDateTime now);
}
