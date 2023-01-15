package com.github.dlots.vehiclefleet.data.repository;

import com.github.dlots.vehiclefleet.data.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
