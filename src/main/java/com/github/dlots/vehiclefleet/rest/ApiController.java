package com.github.dlots.vehiclefleet.rest;

import com.github.dlots.vehiclefleet.data.entity.Driver;
import com.github.dlots.vehiclefleet.data.entity.Enterprise;
import com.github.dlots.vehiclefleet.data.entity.Manager;
import com.github.dlots.vehiclefleet.data.entity.Vehicle;
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

    @GetMapping(API + ENTERPRISES)
    @RolesAllowed(Manager.ROLE)
    List<Enterprise> getManagedEnterprises() {
        Manager manager = (Manager) managerService.getAuthenticatedManager();
        manager = (Manager) managerService.loadUserByUsername(manager.getUsername());
        return manager.getEnterprises();
    }

    @GetMapping(API + DRIVERS)
    @RolesAllowed(Manager.ROLE)
    List<Driver> getManagedEnterprisesDrivers() {
        return getManagedEnterprises().stream().flatMap(enterprise -> enterprise.getDrivers() != null ? enterprise.getDrivers().stream() : Stream.empty()).collect(Collectors.toList());
    }

    @GetMapping(API + VEHICLES)
    @RolesAllowed(Manager.ROLE)
    List<Vehicle> getManagedEnterprisesVehicles() {
        return getManagedEnterprises().stream().flatMap(enterprise -> enterprise.getVehicles() != null ? enterprise.getVehicles().stream() : Stream.empty()).collect(Collectors.toList());
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
        if (vehicle.isPresent()) {
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
