package com.github.dlots.vehiclefleet.data.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.*;
import java.util.Objects;

@Entity
public class Vehicle extends AbstractEntity {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "vehicle_model_id")
    private VehicleModel vehicleModel;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Vehicle vehicle = (Vehicle) o;
        return priceUsd == vehicle.priceUsd && manufactureYear == vehicle.manufactureYear && kmDistanceTravelled == vehicle.kmDistanceTravelled && vin.equals(vehicle.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), vin, priceUsd, manufactureYear, kmDistanceTravelled);
    }
}
