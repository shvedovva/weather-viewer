package com.example.weatherviewer.service;

import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.repository.SessionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private static final String SESSION_COOKIE_NAME = "WEATHER_SESSION_ID";

    private final SessionRepository sessionRepository;

    @Value("${app.session.duration-hours:24}")
    private int sessionDurationHours;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(User user, HttpServletResponse response){
//        UUID sessionId = UUID.randomUUID();
//        LocalDateTime expiresAt = LocalDateTime.now().plusHours(sessionDurationHours);
//        return sessionRepository.save(session);

        // Create new session
        Session session = new Session(user, sessionDurationHours);
        session = sessionRepository.save(session);
        //Create cookie
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, session.getId().toString());
        cookie.setPath("/");
        cookie.setMaxAge(sessionDurationHours * 3600);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return session;
    }

    public Optional<User> getUserFromSession(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                try {
                    UUID sessionId = UUID.fromString(cookie.getValue());
                    Optional<Session> sessionOpt = sessionRepository.findById(sessionId);

                    if (sessionOpt.isPresent()){
                        Session session = sessionOpt.get();

                        if (!session.isExpired()) {
                            session.extendSession(sessionDurationHours);
                            sessionRepository.save(session);
                            return Optional.of(session.getUser());
                        } else {
                            sessionRepository.delete(session);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    public void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return;
        }
        for (Cookie cookie : cookies) {
            if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                try {
                    UUID sessionId = UUID.fromString(cookie.getValue());
                    sessionRepository.deleteById(sessionId);
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format, ignore
                }

                Cookie clearCookie = new Cookie(SESSION_COOKIE_NAME, "");
                clearCookie.setPath("/");
                clearCookie.setMaxAge(0);
                response.addCookie(clearCookie);
                break;
            }
        }
    }
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredSession() {
        sessionRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

//    public Optional<Session> getSessionById(UUID sessionId){
//        if (sessionId == null){
//            return Optional.empty();
//        }
//        return sessionRepository.findByIdAndExpiresAtAfter(sessionId, LocalDateTime.now());
//    }
//
//    public void deleteSession(UUID sessionId){
//        if (sessionId != null){
//            sessionRepository.deleteById(sessionId);
//        }
//    }
//
//    public void deleteExpiredSessions(){
//        int deletedCount = sessionRepository.deleteExpiredSessions(LocalDateTime.now());
//    }

}
