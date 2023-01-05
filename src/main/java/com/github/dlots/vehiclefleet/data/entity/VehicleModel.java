package com.github.dlots.vehiclefleet.data.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Entity
public class VehicleModel extends AbstractEntity {
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
    };

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    };

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VehicleModel that = (VehicleModel) o;
        return gasTankVolumeL == that.gasTankVolumeL && payloadCapacityKg == that.payloadCapacityKg && seatingCapacity == that.seatingCapacity && brandName.equals(that.brandName) && modelName.equals(that.modelName) && vehicleType == that.vehicleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), brandName, modelName, vehicleType, gasTankVolumeL, payloadCapacityKg, seatingCapacity);
    }

    @Override
    public String toString() {
        return getBrandName() + " " + getModelName();
    }
}
