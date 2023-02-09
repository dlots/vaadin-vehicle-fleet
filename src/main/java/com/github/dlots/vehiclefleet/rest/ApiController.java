package com.github.dlots.vehiclefleet.rest;

import com.github.dlots.vehiclefleet.data.Report;
import com.github.dlots.vehiclefleet.data.ReportType;
import com.github.dlots.vehiclefleet.data.entity.*;
import com.github.dlots.vehiclefleet.service.CrmService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class ApiController {
    public static final String API = "/api";
    private static final String VEHICLE_MODELS = "/vehicle_models";
    private static final String ENTERPRISES = "/enterprises";
    private static final String DRIVERS = "/drivers";
    private static final String VEHICLES = "/vehicles";
    private static final String GPS = "/gps";
    private static final String RIDES_TRACK = "/rides_gps_track";
    private static final String RIDES = "/rides";
    private static final String REPORTS = "/reports";
    private static final String DISTANCE = "/distance";
    private static final String ID = "/{id}";

    private final CrmService crmService;

    public ApiController(CrmService crmService) {
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
        return crmService.findCurrentManagerEnterprises();
    }

    @GetMapping(API + DRIVERS)
    @RolesAllowed(Manager.ROLE)
    public List<Driver> getManagedEnterprisesDrivers() {
        return crmService.findManagedEnterprisesDrivers();
    }

    @PostMapping(API + DRIVERS)
    @RolesAllowed(Manager.ROLE)
    public Driver createNewDriver(@RequestBody Driver driver) {
        return crmService.createNewDriver(driver);
    }

    @GetMapping(API + VEHICLES)
    @RolesAllowed(Manager.ROLE)
    public List<Vehicle> getManagedEnterprisesVehicles(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        return crmService.findManagedEnterprisesVehicles(page, size);
    }

    @GetMapping(API + VEHICLES + ID)
    @RolesAllowed(Manager.ROLE)
    public Vehicle getVehicleByIdFromManagedEnterprises(@PathVariable Long id) {
        return crmService.findVehicleFromManagedEnterprises(id);
    }

    @PostMapping(API + VEHICLES)
    @RolesAllowed(Manager.ROLE)
    public Vehicle createNewVehicle(@RequestBody Vehicle vehicle) {
        return crmService.createNewVehicle(vehicle);
    }

    @PutMapping(API + VEHICLES + ID)
    @RolesAllowed(Manager.ROLE)
    public Vehicle updateVehicle(@RequestBody Vehicle vehicle, @PathVariable Long id) {
        return crmService.updateVehicle(vehicle, id);
    }

    @DeleteMapping(API + VEHICLES + ID)
    @RolesAllowed(Manager.ROLE)
    public void deleteVehicle(@PathVariable Long id) {
        crmService.deleteVehicle(id);
    }

    @PostMapping(API + GPS)
    @RolesAllowed(Manager.ROLE)
    public GpsPoint addNewGpsPointForVehicle(@RequestBody GpsPoint gpsPoint) {
        return crmService.createNewGpsPoint(gpsPoint);
    }

    @GetMapping(API + GPS + ID)
    @RolesAllowed(Manager.ROLE)
    public List<GpsPoint> getGpsTrackByVehicleIdAndDateRange(
            @PathVariable Long id,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return crmService.findGpsPointsForVehicleInDateRange(id, start, end);
    }

    @GetMapping(API + RIDES_TRACK + ID)
    @RolesAllowed(Manager.ROLE)
    public List<GpsPoint> getRidesTrackByVehicleIdAndDateRange(
            @PathVariable Long id,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return crmService.findRidesTrackByVehicleIdInDateRange(id, start, end);
    }

    @GetMapping(API + RIDES + ID)
    @RolesAllowed(Manager.ROLE)
    public List<Ride> getRidesByVehicleIdAndDateRange(
            @PathVariable Long id,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return crmService.findRidesByVehicleIdInDateRange(id, start, end);
    }

    @GetMapping(API + REPORTS + DISTANCE + ID)
    @RolesAllowed(Manager.ROLE)
    public Report getDistanceTravelledReportByVehicleId(
            @PathVariable Long id,
            @RequestParam("timeunit") String timeUnit,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return crmService.getReportForVehicleByChronoUnitInDateRange(
                id, ReportType.DISTANCE_TRAVELLED, ChronoUnit.valueOf(timeUnit), start, end);
    }
}
