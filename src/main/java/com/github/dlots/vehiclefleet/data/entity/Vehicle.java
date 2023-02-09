package com.github.dlots.vehiclefleet.data.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dlots.vehiclefleet.util.json.DriverDeserializer;
import com.github.dlots.vehiclefleet.util.json.EnterpriseDeserializer;
import com.github.dlots.vehiclefleet.util.json.VehicleModelDeserializer;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Vehicle extends AbstractEntity {
    public Vehicle() {
        this.gpsTrack = new ArrayList<>();
    }

    public Vehicle(VehicleModel model, Enterprise enterprise, @Nullable List<Driver> drivers, @Nullable Driver activeDriver,
                   String vin, int priceUsd, int manufactureYear, int distanceTravelledKm, @Nullable Instant purchaseDateTimeUtc) {
        this();
        this.vehicleModel = model;
        this.enterprise = enterprise;
        this.drivers = drivers;
        this.activeDriver = activeDriver;
        this.vin = vin;
        this.priceUsd = priceUsd;
        this.manufactureYear = manufactureYear;
        this.distanceTravelledKm = distanceTravelledKm;
        this.purchaseDateTimeUtc = purchaseDateTimeUtc;

    }

    public Vehicle(VehicleModel model, Enterprise enterprise, @Nullable List<Driver> drivers, @Nullable Driver activeDriver,
                   String vin, int priceUsd, int manufactureYear, int distanceTravelledKm, @Nullable Instant purchaseDateTimeUtc,
                   List<GpsPoint> gpsTrack, @Nullable List<Ride> rides) {
        this(model, enterprise, drivers, activeDriver, vin, priceUsd, manufactureYear, distanceTravelledKm, purchaseDateTimeUtc);
        this.gpsTrack = gpsTrack;
        this.rides = rides;
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

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<GpsPoint> gpsTrack;

    @Nullable
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Ride> rides;

    @NotBlank
    @Size(min = 17, max = 17)
    private String vin;

    @Positive
    private int priceUsd;

    @Positive
    private int manufactureYear;

    @PositiveOrZero
    private int distanceTravelledKm;

    public VehicleModel getVehicleModel() {
        return vehicleModel;
    }

    @Nullable
    @JsonIgnore
    private Instant purchaseDateTimeUtc;

    @Nullable
    @Transient
    private ZonedDateTime purchaseDateTimeEnterpriseLocal;

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

    public int getDistanceTravelledKm() {
        return distanceTravelledKm;
    }

    public void setDistanceTravelledKm(int kmMileage) {
        this.distanceTravelledKm = kmMileage;
    }

    @Nullable
    public Instant getPurchaseDateTimeUtc() {
        return purchaseDateTimeUtc;
    }

    public void setPurchaseDateTimeUtc(@Nullable Instant purchaseDateTimeUtc) {
        this.purchaseDateTimeUtc = purchaseDateTimeUtc;
    }

    @Nullable
    public ZonedDateTime getPurchaseDateTimeEnterpriseLocal() {
        return purchaseDateTimeEnterpriseLocal;
    }

    public void resolvePurchaseDateTimeEnterpriseLocal() {
        if (purchaseDateTimeUtc == null) {
            return;
        }
        this.purchaseDateTimeEnterpriseLocal = purchaseDateTimeUtc.atZone(enterprise.getTimeZone().toZoneId());
    }

    public List<GpsPoint> getGpsTrack() {
        return gpsTrack;
    }

    public void setGpsTrack(List<GpsPoint> gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    @Nullable
    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(@Nullable List<Ride> rides) {
        this.rides = rides;
    }

    public void update(Vehicle other) {
        setDrivers(other.getDrivers());
        setActiveDriver(other.getActiveDriver());
        setEnterprise(other.getEnterprise());
        setVin(other.getVin());
        setVehicleModel(other.getVehicleModel());
        setDistanceTravelledKm(other.getDistanceTravelledKm());
        setPriceUsd(other.getPriceUsd());
        setManufactureYear(other.getManufactureYear());
        setPurchaseDateTimeUtc(other.getPurchaseDateTimeUtc());
        setGpsTrack(other.getGpsTrack());
    }

    @PreRemove
    private void removeLinkInActiveDriver() {
        if (activeDriver != null) {
            activeDriver.setVehicle(null);
        }
    }

    @PrePersist
    @PreUpdate
    private void updateRelations() {
        if (drivers != null) {
            drivers.forEach(driver -> driver.setVehicle(this));
        }
        if (activeDriver != null) {
            activeDriver.setVehicle(this);
        }
        if (rides != null) {
            rides.forEach(ride -> ride.setVehicle(this));
        }
        gpsTrack.forEach(gpsPoint -> gpsPoint.setVehicle(this));
    }

    @Override
    public String toString() {
        return String.format("%d %s %s", manufactureYear, vehicleModel, vin);
    }
}
