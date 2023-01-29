package com.github.dlots.vehiclefleet.data.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

@Entity
public class Enterprise extends AbstractEntity {
    public Enterprise() {
    }

    public Enterprise(String name, TimeZone timeZone, @Nullable List<Driver> drivers, @Nullable List<Vehicle> vehicles) {
        this.name = name;
        this.timeZone = timeZone == null ? TimeZone.getTimeZone("UTC") : timeZone;
        this.drivers = drivers;
        this.vehicles = vehicles;
    }

    @NotBlank
    private String name;

    private TimeZone timeZone;

    @OneToMany(mappedBy = "enterprise")
    @Nullable
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("driversIds")
    private List<Driver> drivers = new LinkedList<>();

    @OneToMany(mappedBy = "enterprise")
    @Nullable
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("vehiclesIds")
    private List<Vehicle> vehicles = new LinkedList<>();

    @ManyToMany
    @Nullable
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("managerIds")
    private List<Manager> managers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone == null ? TimeZone.getTimeZone("UTC") : timeZone;
    }

    @Nullable
    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(@Nullable List<Driver> drivers) {
        this.drivers = drivers;
    }

    @Nullable
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(@Nullable List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Nullable
    public List<Manager> getManagers() {
        return managers;
    }

    public void setManagers(@Nullable List<Manager> managers) {
        this.managers = managers;
    }

    @Override
    public String toString() {
        return name;
    }
}
