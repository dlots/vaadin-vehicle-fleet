package com.github.dlots.vehiclefleet.data.repository;

import com.github.dlots.vehiclefleet.data.entity.GpsPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface GpsPointRepository extends JpaRepository<GpsPoint, Long> {
    List<GpsPoint> findByVehicleIdAndTimestampBetween(Long vehicleId, Instant start, Instant end);

    GpsPoint findByVehicleIdAndTimestamp(Long vehicleId, Instant timestamp);
}
