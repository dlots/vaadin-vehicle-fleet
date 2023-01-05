package com.github.dlots.vehiclefleet.data.service;

import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.data.entity.VehicleModel;
import com.github.dlots.vehiclefleet.data.repository.VehicleModelRepository;
import com.github.dlots.vehiclefleet.data.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CrmService {
    private final VehicleRepository vehicleRepository;
    private final VehicleModelRepository vehicleModelRepository;

    public CrmService(VehicleRepository vehicleRepository, VehicleModelRepository vehicleModelRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleModelRepository = vehicleModelRepository;
    }

    public Optional<Vehicle> findVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public void saveVehicle(Vehicle vehicle) {
        vehicleRepository.saveAndFlush(vehicle);
    }

    public void deleteVehicle(Vehicle vehicle) {
        vehicleRepository.delete(vehicle);
    }

    public List<VehicleModel> findAllVehicleModels() {
        return vehicleModelRepository.findAll();
    }

    public void saveVehicleModel(VehicleModel model) {
        vehicleModelRepository.saveAndFlush(model);
    }
}
