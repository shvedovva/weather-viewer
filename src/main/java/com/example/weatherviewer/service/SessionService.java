package com.example.weatherviewer.service;

import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Value;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SessionService {

    private final SessionRepository sessionRepository;
    private final int sessionDurationHours;


    public SessionService(SessionRepository sessionRepository,
                          @Value("${app.session.duration-hours:24}") int sessionDurationHours) {
        this.sessionRepository = sessionRepository;
        this.sessionDurationHours = sessionDurationHours;
    }

    public Session createSession(User user){
        UUID sessionId = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(sessionDurationHours);
        Session session = new Session(sessionId, user, expiresAt);
        return sessionRepository.save(session);
    }

    public Optional<Session> getSessionById(UUID sessionId){
        if (sessionId == null){
            return Optional.empty();
        }
        return sessionRepository.findByIdAndExpiresAtAfter(sessionId, LocalDateTime.now());
    }

    public void deleteSession(UUID sessionId){
        if (sessionId != null){
            sessionRepository.deleteById(sessionId);
        }
    }

    public void deleteExpiredSessions(){
        int deletedCount = sessionRepository.deleteExpiredSessions(LocalDateTime.now());
    }

}
