package com.github.dlots.vehiclefleet.data.entity;

import org.hibernate.type.StringNVarcharType;

public enum VehicleType {
    BUS("Bus"),
    CAR("Car"),
    MOTORCYCLE("Motorcycle"),
    TRUCK("Truck");

    private final String name;

    private VehicleType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
