package com.github.dlots.vehiclefleet.data;

public enum ReportType {
    DISTANCE_TRAVELLED("Distance travelled");

    private final String name;

    ReportType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
