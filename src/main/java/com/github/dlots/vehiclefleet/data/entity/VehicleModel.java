package com.github.dlots.vehiclefleet.data.entity;

import com.github.dlots.vehiclefleet.data.VehicleType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
public class VehicleModel extends AbstractEntity {
    public VehicleModel() {
    }

    public VehicleModel(String brandName, String modelName, VehicleType vehicleType, int gasTankVolumeL, int payloadCapacityKg, int seatingCapacity) {
        this.brandName = brandName;
        this.modelName = modelName;
        this.vehicleType = vehicleType;
        this.gasTankVolumeL = gasTankVolumeL;
        this.payloadCapacityKg = payloadCapacityKg;
        this.seatingCapacity = seatingCapacity;
    }

    @NotBlank
    private String brandName;

    @NotBlank
    private String modelName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Positive
    private int gasTankVolumeL;

    @Positive
    private int payloadCapacityKg;

    @Positive
    private int seatingCapacity;
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getGasTankVolumeL() {
        return gasTankVolumeL;
    }

    public void setGasTankVolumeL(int gasTankVolume) {
        this.gasTankVolumeL = gasTankVolume;
    }

    public int getPayloadCapacityKg() {
        return payloadCapacityKg;
    }

    public void setPayloadCapacityKg(int payloadCapacityKg) {
        this.payloadCapacityKg = payloadCapacityKg;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    @Override
    public String toString() {
        return getBrandName() + " " + getModelName();
    }
}
