package com.manwon.happiness.place.repository;

import com.manwon.happiness.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    boolean existsByNameAndPostalCode(String name, String postalCode);

    Optional<Place> findByNameAndAddress(String name, String address);
}
