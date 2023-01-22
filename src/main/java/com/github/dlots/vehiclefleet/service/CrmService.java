package com.github.dlots.vehiclefleet.service;

import com.github.dlots.vehiclefleet.data.entity.*;
import com.github.dlots.vehiclefleet.data.repository.DriverRepository;
import com.github.dlots.vehiclefleet.data.repository.EnterpriseRepository;
import com.github.dlots.vehiclefleet.data.repository.VehicleModelRepository;
import com.github.dlots.vehiclefleet.data.repository.VehicleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CrmService {
    private final VehicleRepository vehicleRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final DriverRepository driverRepository;
    private final ManagerService managerService;

    public CrmService(VehicleRepository vehicleRepository, VehicleModelRepository vehicleModelRepository,
                      EnterpriseRepository enterpriseRepository, DriverRepository driverRepository,
                      ManagerService managerService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleModelRepository = vehicleModelRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.driverRepository = driverRepository;
        this.managerService = managerService;
    }

    public Optional<Vehicle> findVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
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

    public void saveVehicles(Vehicle... vehicles) {
        vehicleRepository.saveAllAndFlush(List.of(vehicles));
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

    public void saveVehicleModels(VehicleModel... vehicleModels) {
        vehicleModelRepository.saveAllAndFlush(List.of(vehicleModels));
    }

    public Optional<Enterprise> findEnterpriseById(Long id) {
        return enterpriseRepository.findById(id);
    }

    public List<Enterprise> findAllEnterprises() {
        return enterpriseRepository.findAll();
    }

    public void saveEnterprise(Enterprise enterprise) {
        enterpriseRepository.saveAndFlush(enterprise);
    }

    public void saveEnterprises(Enterprise... enterprises) {
        enterpriseRepository.saveAllAndFlush(List.of(enterprises));
    }

    public Optional<Driver> findDriverById(Long id) {
        return driverRepository.findById(id);
    }

    public List<Driver> findAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver saveDriver(Driver driver) {
        return driverRepository.saveAndFlush(driver);
    }

    public void saveDrivers(Driver... drivers) {
        driverRepository.saveAllAndFlush(List.of(drivers));
    }
}
