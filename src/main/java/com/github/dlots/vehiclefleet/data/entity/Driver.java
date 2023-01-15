package com.github.dlots.vehiclefleet.data.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Entity
public class Driver extends AbstractEntity {
    public Driver() {
    }

    public Driver(String name, int salaryUsd, Enterprise enterprise, Vehicle vehicle) {
        this.name = name;
        this.salaryUsd = salaryUsd;
        this.enterprise = enterprise;
        this.vehicle = vehicle;
    }

    @NotBlank
    private String name;

    @PositiveOrZero
    private int salaryUsd;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    @NotNull
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("enterpriseId")
    private Enterprise enterprise;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @Nullable
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("vehicleId")
    private Vehicle vehicle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalaryUsd() {
        return salaryUsd;
    }

    public void setSalaryUsd(int salaryUsd) {
        this.salaryUsd = salaryUsd;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    @Nullable
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(@Nullable Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
