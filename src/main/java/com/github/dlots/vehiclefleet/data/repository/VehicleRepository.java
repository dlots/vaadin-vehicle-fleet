package com.github.dlots.vehiclefleet.data.repository;

import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("SELECT v FROM Vehicle v WHERE v.enterprise.id in :ids")
    Page<Vehicle> findPageByEnterpriseIds(@Param("ids") Iterable<Long> enterpriseIds, Pageable pageRequest);

    @Query("SELECT v FROM Vehicle v WHERE v.enterprise.id in :ids")
    List<Vehicle> findAllByEnterpriseIds(@Param("ids") Iterable<Long> enterpriseIds);
}
