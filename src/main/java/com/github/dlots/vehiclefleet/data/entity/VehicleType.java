package com.github.dlots.vehiclefleet.data.entity;

public enum VehicleType {
    BUS("Bus"),
    CAR("Car"),
    MOTORCYCLE("Motorcycle"),
    TRUCK("Truck");

    private final String name;

    VehicleType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
