package com.github.dlots.vehiclefleet.data.repository;

import com.github.dlots.vehiclefleet.data.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RideRepository extends JpaRepository<Ride, Long> {
    Ride findByStartTimeAfter(Instant start);

    Ride findByEndTimeBefore(Instant start);
}
