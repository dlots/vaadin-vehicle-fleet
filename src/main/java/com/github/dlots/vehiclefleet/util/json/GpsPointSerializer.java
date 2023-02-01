package com.github.dlots.vehiclefleet.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.dlots.vehiclefleet.data.entity.GpsPoint;
import com.vividsolutions.jts.geom.Point;

import java.io.IOException;

public class GpsPointSerializer extends StdSerializer<GpsPoint> {
    public GpsPointSerializer() {
        this(null);
    }

    public GpsPointSerializer(Class<GpsPoint> gpsPointClass) {
        super(gpsPointClass);
    }

    @Override
    public void serialize(GpsPoint gpsPoint, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        Point point = gpsPoint.getPoint();
        jsonGenerator.writeNumberField("latitude", point.getX());
        jsonGenerator.writeNumberField("longitude", point.getY());
        if (gpsPoint.getAddress() != null) {
            jsonGenerator.writeStringField("address", gpsPoint.getAddress());
        }
        serializerProvider.defaultSerializeField("timestamp", gpsPoint.getTimestamp().atZone(gpsPoint.getVehicle().getEnterprise().getTimeZone().toZoneId()), jsonGenerator);
        jsonGenerator.writeEndObject();
    }
}
