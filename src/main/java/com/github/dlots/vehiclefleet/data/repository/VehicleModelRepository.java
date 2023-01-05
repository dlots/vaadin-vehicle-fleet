package com.github.dlots.vehiclefleet.data.repository;

import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.data.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {
}
