package com.github.dlots.vehiclefleet.util;

import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.data.entity.VehicleModel;
import com.github.dlots.vehiclefleet.data.entity.VehicleType;
import com.github.dlots.vehiclefleet.data.service.CrmService;

public class EntityUtil {
    private EntityUtil() {
    }

    public static VehicleModel createAndSaveVehicleModel(CrmService service, String brandName, String modelName, VehicleType type, int tank, int payload, int seating) {
        VehicleModel model = new VehicleModel();
        model.setBrandName(brandName);
        model.setModelName(modelName);
        model.setVehicleType(type);
        model.setGasTankVolumeL(tank);
        model.setPayloadCapacityKg(payload);
        model.setSeatingCapacity(seating);
        service.saveVehicleModel(model);
        return model;
    }

    public static Vehicle createAndSaveVehicle(CrmService service, VehicleModel model, String vin, int price, int year, int distance) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleModel(model);
        vehicle.setVin(vin);
        vehicle.setPriceUsd(price);
        vehicle.setManufactureYear(year);
        vehicle.setKmDistanceTravelled(distance);
        service.saveVehicle(vehicle);
        return vehicle;
    }
}
