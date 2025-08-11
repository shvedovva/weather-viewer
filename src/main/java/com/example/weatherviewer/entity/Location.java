package com.example.weatherviewer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Location name cannot be empty")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User must be specified")
    private User user;

    @Column(precision = 10, scale = 7, nullable = false)
    @NotNull(message = "Latitude must be specified")
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7, nullable = false)
    @NotNull(message = "Longitude must be specified")
    private BigDecimal longitude;
}
