package com.example.weatherviewer.controller;

import com.example.weatherviewer.dto.WeatherDto;
import com.example.weatherviewer.entity.Location;
import com.example.weatherviewer.entity.Session;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.service.LocationService;
import com.example.weatherviewer.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
@Controller
public class HomeController {

    private final SessionService sessionService;
    private final LocationService locationService;

    @Autowired
    public HomeController(SessionService sessionService, LocationService locationService) {
        this.sessionService = sessionService;
        this.locationService = locationService;
    }

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model){
        Optional<User> userOpt = sessionService.getUserFromSession(request);
        if (userOpt.isPresent()){
            User user = userOpt.get();
            model.addAttribute("user", user);

            List<Location> locations = locationService.getUserLocations(user);
            Map<Integer, WeatherDto> weatherMap = locationService.getWeatherForLocations(locations);

            model.addAttribute("locations", locations);
            model.addAttribute("weatherMap", weatherMap);
        }

        return "index";
    }

}
