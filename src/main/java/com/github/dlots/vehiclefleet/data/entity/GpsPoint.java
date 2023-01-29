package com.github.dlots.vehiclefleet.data.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.dlots.vehiclefleet.util.json.GpsPointSerializer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.TimeZone;

@Entity
@JsonSerialize(using = GpsPointSerializer.class)
public class GpsPoint extends AbstractEntity{
    @Transient
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point point;

    public GpsPoint() {
    }

    public GpsPoint(Point point, Instant timestamp) {
        this.point = point;
        this.timestamp = timestamp;
    }

    public static GpsPoint of(double x, double y, Instant timestamp) {
        return new GpsPoint(GEOMETRY_FACTORY.createPoint(new Coordinate(x, y)), timestamp);
    }

    private Instant timestamp;

    @Nullable
    @Transient
    private TimeZone enterpriseTimeZone;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Nullable
    public TimeZone getEnterpriseTimeZone() {
        return enterpriseTimeZone;
    }

    public void setEnterpriseTimeZone(@Nullable TimeZone enterpriseTimeZone) {
        this.enterpriseTimeZone = enterpriseTimeZone;
    }
}
