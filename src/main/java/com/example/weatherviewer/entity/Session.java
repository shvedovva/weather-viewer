package com.example.weatherviewer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User must be specified")
    private User user;

    @Column(name = "expires_at", nullable = false)
    @NotNull(message = "Expiration time must be specified")
    private LocalDateTime expiresAt;

    public Session() {
        this.id = UUID.randomUUID();
    }

    public Session(User user, int hoursToLive){
        this();
        this.user = user;
        this.expiresAt = LocalDateTime.now().plusHours(hoursToLive);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void extendSession(int hours){
        this.expiresAt = LocalDateTime.now().plusHours(hours);
    }
}
