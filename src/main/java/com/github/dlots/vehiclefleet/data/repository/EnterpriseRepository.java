package com.github.dlots.vehiclefleet.data.repository;

import com.github.dlots.vehiclefleet.data.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
}
