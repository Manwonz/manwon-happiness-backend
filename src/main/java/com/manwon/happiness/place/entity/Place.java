package com.manwon.happiness.place.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.manwon.happiness.member.entity.Role.MEMBER;

@Entity
@Table(name = "places")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Column(length = 30, unique = true)
    private String naverPlaceId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 300)
    private String address;

    @Column(nullable = false, length = 10)
    private String postalCode;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isClosed = false;

    private Double lat;
    private Double lng;

    @Column(length = 50)
    private String category;

    @Column(length = 30)
    private String phone;

    @Column(length = 500)
    private String sourceUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now(); // optional: 동시에 세팅
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePostalCode(String newPostalCode) {
        this.postalCode = newPostalCode;
    }
}
