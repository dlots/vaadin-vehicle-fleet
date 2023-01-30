package com.github.dlots.vehiclefleet.service;

import com.github.dlots.vehiclefleet.data.entity.*;
import com.github.dlots.vehiclefleet.data.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CrmService {
    private final VehicleRepository vehicleRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final DriverRepository driverRepository;
    private final GpsPointRepository gpsPointRepository;
    private final RideRepository rideRepository;
    private final ManagerService managerService;

    public CrmService(VehicleRepository vehicleRepository, VehicleModelRepository vehicleModelRepository,
                      EnterpriseRepository enterpriseRepository, DriverRepository driverRepository,
                      GpsPointRepository gpsPointRepository, RideRepository rideRepository,
                      ManagerService managerService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleModelRepository = vehicleModelRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.driverRepository = driverRepository;
        this.gpsPointRepository = gpsPointRepository;
        this.rideRepository = rideRepository;
        this.managerService = managerService;
    }

    public Optional<Vehicle> findVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> findAllVehiclesForEnterprises(List<Enterprise> enterprises) {
        return vehicleRepository.findAllByEnterpriseIds(enterprises.stream().map(AbstractEntity::getId).collect(Collectors.toList()));
    }

    public List<Vehicle> findAllVehiclesForManagedEnterprises() {
        return findAllVehiclesForEnterprises(managerService.getManagedEnterprises());
    }

    public List<Vehicle> findAllVehiclesForEnterprisesByPage(List<Enterprise> enterprises, int page, int size) {
        return vehicleRepository.findPageByEnterpriseIds(
                        enterprises.stream().map(AbstractEntity::getId).collect(Collectors.toList()),
                        PageRequest.of(page, size))
                .getContent();
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.saveAndFlush(vehicle);
    }

    public void deleteVehicle(Vehicle vehicle) {
        vehicleRepository.delete(vehicle);
    }

    public void deleteVehicleById(Long id) {
        vehicleRepository.deleteById(id);
    }

    public Optional<VehicleModel> findVehicleModelById(Long id) {
        return vehicleModelRepository.findById(id);
    }

    public List<VehicleModel> findAllVehicleModels() {
        return vehicleModelRepository.findAll();
    }

    public void saveVehicleModel(VehicleModel model) {
        vehicleModelRepository.saveAndFlush(model);
    }

    public Optional<Enterprise> findEnterpriseById(Long id) {
        return enterpriseRepository.findById(id);
    }

    public void saveEnterprises(Enterprise... enterprises) {
        enterpriseRepository.saveAllAndFlush(List.of(enterprises));
    }

    public Optional<Driver> findDriverById(Long id) {
        return driverRepository.findById(id);
    }

    public Driver saveDriver(Driver driver) {
        return driverRepository.saveAndFlush(driver);
    }

    public void saveDrivers(Driver... drivers) {
        driverRepository.saveAllAndFlush(List.of(drivers));
    }

    public List<GpsPoint> findGpsPointsForVehicleInDateRange(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        ZoneId zoneId = enterpriseRepository.findTimeZoneByVehicleId(vehicleId).toZoneId();
        return gpsPointRepository.findByVehicleIdAndTimestampBetween(
                vehicleId, ZonedDateTime.of(start, zoneId).toInstant(), ZonedDateTime.of(end, zoneId).toInstant());
    }

    public List<GpsPoint> findRidesByVehicleIdInDateRange(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        ZoneId zoneId = enterpriseRepository.findTimeZoneByVehicleId(vehicleId).toZoneId();
        Instant startInstant = ZonedDateTime.of(start, zoneId).toInstant();
        Instant endInstant = ZonedDateTime.of(end, zoneId).toInstant();
        Ride lowerBound = rideRepository.findByStartTimeAfter(startInstant);
        Ride upperBound = rideRepository.findByEndTimeBefore(endInstant);
        if (lowerBound == null || upperBound == null) {
            return Collections.emptyList();
        }
        return gpsPointRepository.findByVehicleIdAndTimestampBetween(
                vehicleId, lowerBound.getStartTime(), upperBound.getEndTime());
    }
}
