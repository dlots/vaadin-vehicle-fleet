package com.github.dlots.vehiclefleet.data.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dlots.vehiclefleet.util.json.VehicleDeserializer;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
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
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("vehicleId")
    @JsonDeserialize(using = VehicleDeserializer.class)
    private Vehicle vehicle;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    @Transient
    private GpsPoint startPoint;

    @Transient
    private GpsPoint endPoint;

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

    public GpsPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(GpsPoint startPoint) {
        this.startPoint = startPoint;
        populateAddress(startPoint);
    }

    public GpsPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(GpsPoint endPoint) {
        this.endPoint = endPoint;
        populateAddress(endPoint);
    }

    private static void populateAddress(GpsPoint point) {
        if (point == null) {
            return;
        }
        if (point.getAddress() == null || point.getAddress().isEmpty()) {
            point.populateAddressFromCoordinates();
        }
    }
}
