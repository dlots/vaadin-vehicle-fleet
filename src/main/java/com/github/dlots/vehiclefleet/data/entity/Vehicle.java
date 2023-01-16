package com.github.dlots.vehiclefleet.data.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dlots.vehiclefleet.util.json.DriverDeserializer;
import com.github.dlots.vehiclefleet.util.json.EnterpriseDeserializer;
import com.github.dlots.vehiclefleet.util.json.VehicleModelDeserializer;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Vehicle extends AbstractEntity {
    public Vehicle() {
    }

    public Vehicle(VehicleModel model, Enterprise enterprise, @Nullable List<Driver> drivers, @Nullable Driver activeDriver, String vin, int priceUsd, int manufactureYear, int kmDistanceTravelled) {
        this.vehicleModel = model;
        this.enterprise = enterprise;
        this.drivers = drivers;
        this.activeDriver = activeDriver;
        this.vin = vin;
        this.priceUsd = priceUsd;
        this.manufactureYear = manufactureYear;
        this.kmDistanceTravelled = kmDistanceTravelled;
    }

    @NotNull
    @ManyToOne
    @JoinColumn(name = "vehicle_model_id")
    @JsonDeserialize(using = VehicleModelDeserializer.class)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("modelId")
    private VehicleModel vehicleModel;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    @NotNull
    @JsonDeserialize(using = EnterpriseDeserializer.class)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("enterpriseId")
    private Enterprise enterprise;

    @OneToMany(mappedBy = "vehicle")
    @Nullable
    @JsonDeserialize(contentUsing = DriverDeserializer.class)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("driverIds")
    private List<Driver> drivers = new LinkedList<>();

    @Nullable
    @OneToOne
    @JoinColumn(name = "active_driver_id")
    @JsonDeserialize(using = DriverDeserializer.class)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("activeDriverId")
    private Driver activeDriver;

    @NotBlank
    @Size(min = 17, max = 17)
    private String vin;

    @Positive
    private int priceUsd;

    @Positive
    private int manufactureYear;

    @PositiveOrZero
    private int kmDistanceTravelled;

    public VehicleModel getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(VehicleModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    @Nullable
    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(@Nullable List<Driver> drivers) {
        this.drivers = drivers;
    }

    @Nullable
    public Driver getActiveDriver() {
        return activeDriver;
    }

    public void setActiveDriver(@Nullable Driver activeDriver) {
        this.activeDriver = activeDriver;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public int getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(int priceUsd) {
        this.priceUsd = priceUsd;
    }

    public int getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(int manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public int getKmDistanceTravelled() {
        return kmDistanceTravelled;
    }

    public void setKmDistanceTravelled(int kmMileage) {
        this.kmDistanceTravelled = kmMileage;
    }

    public void update(Vehicle other) {
        setDrivers(other.getDrivers());
        setActiveDriver(other.getActiveDriver());
        setEnterprise(other.getEnterprise());
        setVin(other.getVin());
        setVehicleModel(other.getVehicleModel());
        setKmDistanceTravelled(other.getKmDistanceTravelled());
        setPriceUsd(other.getPriceUsd());
        setManufactureYear(other.getManufactureYear());
    }

    @PreRemove
    private void removeLinkInActiveDriver() {
        if (activeDriver != null) {
            activeDriver.setVehicle(null);
        }
    }

    @PrePersist
    @PreUpdate
    private void updateDrivers() {
        if (drivers != null) {
            drivers.forEach(driver -> driver.setVehicle(this));
        }
        if (activeDriver != null) {
            activeDriver.setVehicle(this);
        }
    }
}
