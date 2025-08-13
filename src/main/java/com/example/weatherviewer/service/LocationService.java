package com.example.weatherviewer.service;

import com.example.weatherviewer.dto.LocationSearchDto;
import com.example.weatherviewer.dto.WeatherDto;
import com.example.weatherviewer.entity.Location;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.LocationAlreadyExistsException;
import com.example.weatherviewer.exception.LocationNotFoundException;
import com.example.weatherviewer.exception.UnauthorisedException;
import com.example.weatherviewer.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final WeatherApiService weatherApiService;

    @Autowired
    public LocationService(LocationRepository locationRepository, WeatherApiService weatherApiService) {
        this.locationRepository = locationRepository;
        this.weatherApiService = weatherApiService;
    }

    public List<LocationSearchDto> searchLocations(String query) {
        return weatherApiService.searchLocations(query);
    }

    public Location addLocation(User user, String name, BigDecimal latitude,
                                BigDecimal longitude, String country, String state) {
        if (locationRepository.existsByUserAndLatitudeAndLongitude(user, latitude, longitude)) {
            throw new LocationAlreadyExistsException("This location is already in your list");
        }
        Location location = new Location();
        location.setName(name);
        location.setUser(user);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setCountry(country);
        location.setState(state);

        return locationRepository.save(location);
    }

    public List<Location> getUserLocations(User user) {
        return locationRepository.findByUserOrderByName(user);
    }

    public Map<Integer, WeatherDto> getWeatherForLocations(List<Location> locations){
        Map<Integer, WeatherDto> weatherMap = new ConcurrentHashMap<>();

        for (Location location : locations) {
            try {
                WeatherDto weather = weatherApiService.getWeather(location.getLatitude(), location.getLongitude());
                if (weather != null) {
                    weatherMap.put(location.getId(), weather);
                }
            } catch (Exception e) {
                System.err.println("Failed to get weather for location " + location.getName() + ": " + e.getMessage());
            }
        }
        return weatherMap;
    }
    public void deleteLocation(Integer locationId, User user) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(()-> new LocationNotFoundException("Location not found"));

        if (!location.getUser().getId().equals(user.getId())) {
            throw new UnauthorisedException("You don't have permission to delete this location");
        }

        locationRepository.delete(location);
    }
}
