package com.github.dlots.vehiclefleet.data.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
public class Ride extends AbstractEntity {
    public Ride() {
    }

    public static Ride of(Instant start, Instant end) {
        Ride ride = new Ride();
        ride.setStartTime(start);
        ride.setEndTime(end);
        return ride;
    }

    @NotNull
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
