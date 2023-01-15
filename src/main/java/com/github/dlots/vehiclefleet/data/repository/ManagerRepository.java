package com.github.dlots.vehiclefleet.data.repository;

import com.github.dlots.vehiclefleet.data.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Manager findByUsername(String username);
}
