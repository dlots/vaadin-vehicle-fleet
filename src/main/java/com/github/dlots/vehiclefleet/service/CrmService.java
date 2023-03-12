package com.github.dlots.vehiclefleet.service;

import com.github.dlots.vehiclefleet.data.entity.*;
import com.github.dlots.vehiclefleet.data.repository.*;
import com.github.dlots.vehiclefleet.service.report.Report;
import com.github.dlots.vehiclefleet.service.report.ReportType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Vehicle findVehicleFromManagedEnterprises(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isPresent() && hasAccessToEnterprise(vehicle.get().getEnterprise().getId())) {
            Vehicle v = vehicle.get();
            v.resolvePurchaseDateTimeEnterpriseLocal();
            return v;
        }
        return null;
    }

    public List<Vehicle> findManagedEnterprisesVehicles(Integer page, Integer size) {
        List<Vehicle> result;
        if (page == null || size == null) {
            result = findAllVehiclesForManagedEnterprises();
        } else {
            result = findAllVehiclesForEnterprisesByPage(page, size);
        }
        result.forEach(Vehicle::resolvePurchaseDateTimeEnterpriseLocal);
        return result;
    }

    public List<Vehicle> findVehiclesForEnterprise(Long enterpriseId) {
        if (!hasAccessToEnterprise(enterpriseId)) {
            return Collections.emptyList();
        }
        return vehicleRepository.findAllByEnterpriseIds(List.of(enterpriseId));
    }

    public List<Vehicle> findAllVehiclesForManagedEnterprises() {
        return vehicleRepository.findAllByEnterpriseIds(
                findCurrentManagerEnterprises().stream().map(AbstractEntity::getId).collect(Collectors.toList()));
    }

    public List<Vehicle> findAllVehiclesForEnterprisesByPage(int page, int size) {
        return vehicleRepository.findPageByEnterpriseIds(
                        findCurrentManagerEnterprises().stream().map(AbstractEntity::getId).collect(Collectors.toList()),
                        PageRequest.of(page, size))
                .getContent();
    }

    public Vehicle createNewVehicle(Vehicle vehicle) {
        if (hasAccessToEnterprise(vehicle.getEnterprise().getId())) {
            return saveVehicle(vehicle);
        }
        return null;
    }

    public Vehicle updateVehicle(Vehicle newVehicle, Long id) {
        Optional<Vehicle> existingVehicle = findVehicleById(id);
        if (existingVehicle.isPresent() && hasAccessToEnterprise(existingVehicle.get().getEnterprise().getId())) {
            existingVehicle.get().update(newVehicle);
            return saveVehicle(existingVehicle.get());
        }
        newVehicle.setId(id);
        return saveVehicle(newVehicle);
    }

    public void deleteVehicle(Long id) {
        Optional<Vehicle> vehicle = findVehicleById(id);
        if (vehicle.isPresent() && hasAccessToEnterprise(vehicle.get().getEnterprise().getId())) {
            vehicleRepository.deleteById(id);
        }
    }

    public void deleteVehicle(Vehicle vehicle) {
        vehicleRepository.delete(vehicle);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.saveAndFlush(vehicle);
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
        if (hasAccessToEnterprise(id)) {
            return enterpriseRepository.findById(id);
        }
        return Optional.empty();
    }

    public List<Enterprise> findCurrentManagerEnterprises() {
        Manager manager = (Manager) managerService.getAuthenticatedManager();
        return enterpriseRepository.findAllByManagers_Id(manager.getId());
    }

    public void saveEnterprises(Enterprise... enterprises) {
        enterpriseRepository.saveAllAndFlush(List.of(enterprises));
    }

    public Optional<Driver> findDriverById(Long id) {
        return driverRepository.findById(id);
    }

    public List<Driver> findManagedEnterprisesDrivers() {
        return findCurrentManagerEnterprises().stream()
                .flatMap(enterprise -> enterprise.getDrivers() != null ? enterprise.getDrivers().stream() : Stream.empty())
                .collect(Collectors.toList());
    }

    public Driver createNewDriver(Driver driver) {
        if (hasAccessToEnterprise(driver.getEnterprise().getId())) {
            return driverRepository.saveAndFlush(driver);
        }
        return null;
    }

    public void saveDrivers(Driver... drivers) {
        driverRepository.saveAll(List.of(drivers));
    }

    public List<GpsPoint> createNewGpsPoints(GpsPoint[] gpsPoints) {
        if (hasAccessToVehicle(gpsPoints[0].getVehicle().getId())) {
            return gpsPointRepository.saveAllAndFlush(Arrays.asList(gpsPoints));
        }
        return null;
    }

    public List<GpsPoint> findGpsPointsForVehicleInUtcRange(Long vehicleId, Instant start, Instant end) {
        if (!hasAccessToVehicle(vehicleId)) {
            return Collections.emptyList();
        }
        return gpsPointRepository.findByVehicleIdAndTimestampBetween(vehicleId, start, end);
    }

    public List<GpsPoint> findGpsPointsForVehicleInDateRange(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        if (!hasAccessToVehicle(vehicleId)) {
            return Collections.emptyList();
        }
        ZoneId zoneId = enterpriseRepository.findTimeZoneByVehicleId(vehicleId).toZoneId();
        return gpsPointRepository.findByVehicleIdAndTimestampBetween(
                vehicleId, ZonedDateTime.of(start, zoneId).toInstant(), ZonedDateTime.of(end, zoneId).toInstant());
    }

    public Ride createNewRide(Ride ride) {
        if (hasAccessToVehicle(ride.getVehicle().getId())) {
            return rideRepository.saveAndFlush(ride);
        }
        return null;
    }

    public List<GpsPoint> findRidesTrackByVehicleIdInDateRange(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        if (!hasAccessToVehicle(vehicleId)) {
            return Collections.emptyList();
        }
        List<Ride> rides = findRidesByVehicleIdAndLocalDateRange(vehicleId, start, end);
        Ride lowerBound = rides.get(0);
        Ride upperBound = rides.get(rides.size() - 1);
        if (lowerBound == null || upperBound == null) {
            return Collections.emptyList();
        }
        return gpsPointRepository.findByVehicleIdAndTimestampBetween(
                vehicleId, lowerBound.getStartTime(), upperBound.getEndTime());
    }

    public List<Ride> findRidesByVehicleId(Long vehicleId) {
        if (!hasAccessToVehicle(vehicleId)) {
            return Collections.emptyList();
        }
        return rideRepository.findAllByVehicle_Id(vehicleId);
    }

    public List<Ride> findRidesByVehicleIdInDateRange(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        if (!hasAccessToVehicle(vehicleId)) {
            return Collections.emptyList();
        }
        List<Ride> rides = findRidesByVehicleIdAndLocalDateRange(vehicleId, start, end);
        for (Ride ride : rides) {
            ride.setStartPoint(gpsPointRepository.findByVehicleIdAndTimestamp(vehicleId, ride.getStartTime()));
            ride.setEndPoint(gpsPointRepository.findByVehicleIdAndTimestamp(vehicleId, ride.getEndTime()));
        }
        return rides;
    }

    private List<Ride> findRidesByVehicleIdAndLocalDateRange(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        Pair<Instant, Instant> utcRange = getUtcRange(vehicleId, start, end);
        return rideRepository.findByVehicleIdAndStartTimeAfterAndEndTimeBefore(
                vehicleId, utcRange.getFirst(), utcRange.getSecond());
    }

    public Report getReportForVehicleByChronoUnitInDateRange(
            Long vehicleId, ReportType reportType, ChronoUnit chronoUnit, LocalDateTime start, LocalDateTime end) {
        if (!hasAccessToVehicle(vehicleId)) {
            return null;
        }
        Pair<Instant, Instant> utcRange = getUtcRange(vehicleId, start, end);
        return Report.of(
                vehicleId,
                reportType,
                chronoUnit,
                utcRange.getFirst(),
                utcRange.getSecond(),
                this
        );
    }

    public Double getDistanceTravelledKmForVehicleInTimeRange(Long vehicleId, Instant start, Instant end) {
        List<GpsPoint> gpsTrack = findGpsPointsForVehicleInUtcRange(vehicleId, start, end);
        double result = 0;
        for (int i = 0; i < gpsTrack.size() - 1; i++) {
            result += gpsTrack.get(i).getDistanceKm(gpsTrack.get(i + 1));
        }
        return result;
    }

    private Pair<Instant, Instant> getUtcRange(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        ZoneId zoneId = enterpriseRepository.findTimeZoneByVehicleId(vehicleId).toZoneId();
        Instant startInstant = ZonedDateTime.of(start, zoneId).toInstant();
        Instant endInstant = ZonedDateTime.of(end, zoneId).toInstant();
        return Pair.of(startInstant, endInstant);
    }

    private boolean hasAccessToEnterprise(Long enterpriseId) {
        return findCurrentManagerEnterprises().stream().anyMatch(e -> e.getId().equals(enterpriseId));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasAccessToVehicle(Long vehicleId) {
        Optional<Vehicle> vehicle = findVehicleById(vehicleId);
        return vehicle.isPresent() && hasAccessToEnterprise(vehicle.get().getEnterprise().getId());
    }
}
