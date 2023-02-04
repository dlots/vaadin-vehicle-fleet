package com.github.dlots.vehiclefleet.data.repository;

import com.github.dlots.vehiclefleet.data.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findAllByVehicle_Id(Long vehicleId);

    List<Ride> findByVehicleIdAndStartTimeAfterAndEndTimeBefore(Long vehicleId, Instant start, Instant end);
}
