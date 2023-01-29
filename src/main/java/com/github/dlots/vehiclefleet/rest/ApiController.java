package com.github.dlots.vehiclefleet.rest;

import com.github.dlots.vehiclefleet.data.entity.*;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.service.ManagerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ApiController {
    public static final String API = "/api";
    public static final String VEHICLE_MODELS = "/vehicle_models";
    public static final String ENTERPRISES = "/enterprises";
    public static final String DRIVERS = "/drivers";
    public static final String VEHICLES = "/vehicles";
    public static final String GPS = "/gps";
    public static final String ID = "/{id}";

    private final ManagerService managerService;
    private final CrmService crmService;

    public ApiController(ManagerService managerService, CrmService crmService) {
        this.managerService = managerService;
        this.crmService = crmService;
    }

    @GetMapping(API + VEHICLE_MODELS)
    @RolesAllowed(Manager.ROLE)
    public List<VehicleModel> getVehicleModels() {
        return crmService.findAllVehicleModels();
    }

    @GetMapping(API + ENTERPRISES)
    @RolesAllowed(Manager.ROLE)
    public List<Enterprise> getManagedEnterprises() {
        return managerService.getManagedEnterprises();
    }

    @GetMapping(API + DRIVERS)
    @RolesAllowed(Manager.ROLE)
    public List<Driver> getManagedEnterprisesDrivers() {
        return getManagedEnterprises().stream().flatMap(enterprise -> enterprise.getDrivers() != null ? enterprise.getDrivers().stream() : Stream.empty()).collect(Collectors.toList());
    }

    @PostMapping(API + DRIVERS)
    @RolesAllowed(Manager.ROLE)
    public Driver createNewDriver(@RequestBody Driver newDriver) {
        if (isEnterpriseOwnedByManager(newDriver.getEnterprise().getId())) {
            return crmService.saveDriver(newDriver);
        }
        return null;
    }

    @GetMapping(API + VEHICLES)
    @RolesAllowed(Manager.ROLE)
    public List<Vehicle> getManagedEnterprisesVehicles(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        final List<Vehicle> result;
        List<Enterprise> enterprises = getManagedEnterprises();
        if (page == null || size == null) {
            result = crmService.findAllVehiclesForEnterprises(enterprises);
        } else {
            result = crmService.findAllVehiclesForEnterprisesByPage(enterprises, page, size);
        }
        result.forEach(Vehicle::resolvePurchaseDateTimeEnterpriseLocal);
        return result;
    }

    @GetMapping(API + VEHICLES + ID)
    @RolesAllowed(Manager.ROLE)
    public Vehicle getVehicleByIdFromManagedEnterprises(@PathVariable Long id) {
        Optional<Vehicle> vehicle = crmService.findVehicleById(id);
        if (vehicle.isPresent() && isEnterpriseOwnedByManager(vehicle.get().getEnterprise().getId())) {
            Vehicle v = vehicle.get();
            v.resolvePurchaseDateTimeEnterpriseLocal();
            return v;
        }
        return null;
    }

    @PostMapping(API + VEHICLES)
    @RolesAllowed(Manager.ROLE)
    public Vehicle createNewVehicle(@RequestBody Vehicle newVehicle) {
        if (isEnterpriseOwnedByManager(newVehicle.getEnterprise().getId())) {
            return crmService.saveVehicle(newVehicle);
        }
        return null;
    }

    @PutMapping(API + VEHICLES + ID)
    @RolesAllowed(Manager.ROLE)
    public Vehicle replaceVehicle(@RequestBody Vehicle newVehicle, @PathVariable Long id) {
        Optional<Vehicle> vehicle = crmService.findVehicleById(id);
        if (vehicle.isPresent() && isEnterpriseOwnedByManager(vehicle.get().getEnterprise().getId())) {
            vehicle.get().update(newVehicle);
            return crmService.saveVehicle(vehicle.get());
        }
        newVehicle.setId(id);
        return crmService.saveVehicle(newVehicle);
    }

    @DeleteMapping(API + VEHICLES + ID)
    @RolesAllowed(Manager.ROLE)
    public void deleteVehicle(@PathVariable Long id) {
        Optional<Vehicle> vehicle = crmService.findVehicleById(id);
        if (vehicle.isPresent() && isEnterpriseOwnedByManager(vehicle.get().getEnterprise().getId())) {
            crmService.deleteVehicleById(id);
        }
    }

    @GetMapping(API + GPS + ID)
    @RolesAllowed(Manager.ROLE)
    public List<GpsPoint> getGpsTrackByVehicleIdAndDateRange(
            @PathVariable Long id,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return crmService.findGpsPointsForVehicleInDateRange(id, start, end);
    }

    private boolean isEnterpriseOwnedByManager(Long enterpriseId) {
        return getManagedEnterprises().stream().anyMatch(e -> e.getId().equals(enterpriseId));
    }
}
