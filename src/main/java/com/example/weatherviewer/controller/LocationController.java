package com.example.weatherviewer.controller;

import com.example.weatherviewer.dto.LocationSearchDto;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.service.LocationService;
import com.example.weatherviewer.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/locations")
public class LocationController {

    private final SessionService sessionService;
    private final LocationService locationService;

    @Autowired
    public LocationController(SessionService sessionService, LocationService locationService) {
        this.sessionService = sessionService;
        this.locationService = locationService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query,
                         HttpServletRequest request,
                         Model model) {

        Optional<User> userOpt = sessionService.getUserFromSession(request);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        model.addAttribute("user", userOpt.get());
        model.addAttribute("query", query);

        if (query != null && !query.trim().isEmpty()) {
            List<LocationSearchDto> results = locationService.searchLocations(query);
            model.addAttribute("results", results);
        }
        return "locations/search";
    }

    @PostMapping("/add")
    public String addLocation(@RequestParam String name,
                              @RequestParam BigDecimal latitude,
                              @RequestParam BigDecimal longitude,
                              @RequestParam(required = false) String country,
                              @RequestParam(required = false) String state,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = sessionService.getUserFromSession(request);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        try {
            locationService.addLocation(userOpt.get(), name, latitude, longitude, country, state);
            redirectAttributes.addFlashAttribute("success", "Location added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteLocation(@PathVariable Integer id,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = sessionService.getUserFromSession(request);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        try {
            locationService.deleteLocation(id, userOpt.get());
            redirectAttributes.addFlashAttribute("success", "Location removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }
}