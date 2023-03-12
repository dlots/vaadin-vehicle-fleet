package com.github.dlots.vehiclefleet.service.report;

public enum ReportType {
    DISTANCE_TRAVELLED("Distance travelled, km");

    private final String name;

    ReportType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
