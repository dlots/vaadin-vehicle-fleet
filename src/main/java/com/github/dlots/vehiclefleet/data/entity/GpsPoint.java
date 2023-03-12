package com.github.dlots.vehiclefleet.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.dlots.vehiclefleet.util.json.GpsPointSerializer;
import com.github.dlots.vehiclefleet.util.YandexMapsHandler;
import com.github.dlots.vehiclefleet.util.json.PointDeserializer;
import com.github.dlots.vehiclefleet.util.json.VehicleDeserializer;
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

    @Transient
    private static final YandexMapsHandler YANDEX_MAPS_HANDLER = new YandexMapsHandler();

    public GpsPoint() {
    }

    public GpsPoint(Point point, Instant timestamp) {
        this.point = point;
        this.timestamp = timestamp;
    }

    public static GpsPoint of(double x, double y, Instant timestamp) {
        return new GpsPoint(GEOMETRY_FACTORY.createPoint(new Coordinate(x, y)), timestamp);
    }

    public static Point createPoint(Double x, Double y) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(x, y));
    }

    @NotNull
    @Column(columnDefinition = "geometry(Point,4326)")
    @JsonDeserialize(using = PointDeserializer.class)
    @SuppressWarnings("com.haulmont.jpb.UnsupportedTypeWithoutConverterInspection")
    private Point point;

    @Transient
    @JsonIgnore
    private String address;

    private Instant timestamp;

    @Nullable
    @Transient
    @JsonIgnore
    private TimeZone enterpriseTimeZone;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonProperty("vehicleId")
    @JsonDeserialize(using = VehicleDeserializer.class)
    private Vehicle vehicle;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public double getLatitude() {
        return point.getX();
    }

    public double getLongitude() {
        return point.getY();
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

    public String getAddress() {
        return address;
    }

    public void populateAddressFromCoordinates() {
        address = YANDEX_MAPS_HANDLER.getAddressFromCoordinates(point.getX(), point.getY());
    }

    /**
     * <a href="shorturl.at/bexzU">Copied from StackOverflow</a>
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @return Distance in Meters
     */
    public static double haversineDistanceKm(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return Math.sqrt(Math.pow(distance, 2));
    }

    public double getDistanceKm(GpsPoint other) {
        return haversineDistanceKm(this.getLatitude(), other.getLatitude(), this.getLongitude(), other.getLongitude());
    }
}
