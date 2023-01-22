package com.github.dlots.vehiclefleet.rest;

import com.github.dlots.vehiclefleet.data.entity.*;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.service.ManagerService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
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
    public static final String ID = "/{id}";

    private final ManagerService managerService;
    private final CrmService crmService;

    public ApiController(ManagerService managerService, CrmService crmService) {
        this.managerService = managerService;
        this.crmService = crmService;
    }

    @GetMapping(API + VEHICLE_MODELS)
    @RolesAllowed(Manager.ROLE)
    List<VehicleModel> getVehicleModels() {
        return crmService.findAllVehicleModels();
    }

    @GetMapping(API + ENTERPRISES)
    @RolesAllowed(Manager.ROLE)
    List<Enterprise> getManagedEnterprises() {
        return managerService.getManagedEnterprises();
    }

    @GetMapping(API + DRIVERS)
    @RolesAllowed(Manager.ROLE)
    List<Driver> getManagedEnterprisesDrivers() {
        return getManagedEnterprises().stream().flatMap(enterprise -> enterprise.getDrivers() != null ? enterprise.getDrivers().stream() : Stream.empty()).collect(Collectors.toList());
    }

    @PostMapping(API + DRIVERS)
    @RolesAllowed(Manager.ROLE)
    Driver createNewDriver(@RequestBody Driver newDriver) {
        if (isEnterpriseOwnedByManager(newDriver.getEnterprise().getId())) {
            return crmService.saveDriver(newDriver);
        }
        return null;
    }

    @GetMapping(API + VEHICLES)
    @RolesAllowed(Manager.ROLE)
    List<Vehicle> getManagedEnterprisesVehicles(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        List<Enterprise> enterprises = getManagedEnterprises();
        if (page == null || size == null) {
            return crmService.findAllVehiclesForEnterprises(enterprises);
        }
        return crmService.findAllVehiclesForEnterprisesByPage(enterprises, page, size);
    }

    @GetMapping(API + VEHICLES + ID)
    @RolesAllowed(Manager.ROLE)
    Vehicle getVehicleByIdFromManagedEnterprises(@PathVariable Long id) {
        Optional<Vehicle> vehicle = crmService.findVehicleById(id);
        if (vehicle.isPresent() && isEnterpriseOwnedByManager(vehicle.get().getEnterprise().getId())) {
            return vehicle.get();
        }
        return null;
    }

    @PostMapping(API + VEHICLES)
    @RolesAllowed(Manager.ROLE)
    Vehicle createNewVehicle(@RequestBody Vehicle newVehicle) {
        if (isEnterpriseOwnedByManager(newVehicle.getEnterprise().getId())) {
            return crmService.saveVehicle(newVehicle);
        }
        return null;
    }

    @PutMapping(API + VEHICLES + ID)
    @RolesAllowed(Manager.ROLE)
    Vehicle replaceVehicle(@RequestBody Vehicle newVehicle, @PathVariable Long id) {
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
    void deleteVehicle(@PathVariable Long id) {
        Optional<Vehicle> vehicle = crmService.findVehicleById(id);
        if (vehicle.isPresent() && isEnterpriseOwnedByManager(vehicle.get().getEnterprise().getId())) {
            crmService.deleteVehicleById(id);
        }
    }

    private boolean isEnterpriseOwnedByManager(Long enterpriseId) {
        return getManagedEnterprises().stream().anyMatch(e -> e.getId().equals(enterpriseId));
    }
}
