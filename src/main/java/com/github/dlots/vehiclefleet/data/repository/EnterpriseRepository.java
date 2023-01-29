package com.github.dlots.vehiclefleet.data.repository;

import com.github.dlots.vehiclefleet.data.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.TimeZone;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
    List<Enterprise> findAllByManagers_Id(Long managerId);

    @Query("SELECT e.timeZone FROM Enterprise e JOIN e.vehicles v WHERE v.id = :vehicleId")
    TimeZone findTimeZoneByVehicleId(@Param("vehicleId") Long vehicleId);
}
