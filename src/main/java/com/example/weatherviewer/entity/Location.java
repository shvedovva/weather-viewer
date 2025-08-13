package com.example.weatherviewer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor

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

    @Column(name = "country")
    private String country;

    @Column(name = "state")
    private String state;

    public Location(String name, User user, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDisplayName() {
        StringBuilder sb = new StringBuilder(this.name);
        if (this.state != null && !this.state.isEmpty()) {
            sb.append(", ").append(this.state);
        }
        if (this.country != null && !this.country.isEmpty()) {
            sb.append(", ").append(this.country);
        }
        return sb.toString();
    }
}
