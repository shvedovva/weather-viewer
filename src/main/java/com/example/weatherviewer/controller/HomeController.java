package com.example.weatherviewer.controller;

import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class HomeController {

    private final SessionService sessionService;

    public HomeController(SessionService sessionService) {
        this.sessionService = sessionService;
    }
    @GetMapping("/")
    public String home(HttpServletRequest request, Model model){
        User currentUser = getCurrentUser(request);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("title", "Главная");
        model.addAttribute("locations", Collections.emptyList());
        return "index";
    }

    private User getCurrentUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if ("SID".equals(cookie.getName())){
                    try {
                        UUID sessionId = UUID.fromString(cookie.getValue());
                        Optional<Session> sessionOpt = sessionService.getSessionById(sessionId);
                        return sessionOpt.map(Session::getUser).orElse(null);
                    } catch (IllegalArgumentException e) {
                        // Невалидный UUID
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
