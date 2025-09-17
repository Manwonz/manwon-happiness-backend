package com.manwon.happiness.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.manwon.happiness.user.entity.Role.USER;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.role == null) this.role = USER;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateLastLogin(LocalDateTime now) {
        this.lastLoginAt = now;
    }
}
