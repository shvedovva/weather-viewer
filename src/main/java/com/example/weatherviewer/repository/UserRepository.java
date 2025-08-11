package com.example.weatherviewer.repository;

import com.example.weatherviewer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
}
