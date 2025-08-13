package com.example.weatherviewer.repository;

import com.example.weatherviewer.entity.Location;
import com.example.weatherviewer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByUserOrderByName(User user);
    boolean existsByUserAndLatitudeAndLongitude(User user, BigDecimal latitude, BigDecimal longitude);
}
