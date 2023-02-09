package com.github.dlots.vehiclefleet.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dlots.vehiclefleet.data.entity.GpsPoint;
import com.vividsolutions.jts.geom.Point;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PointDeserializer extends JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String[] coordinates = jsonParser.getText().split(",");
        return GpsPoint.createPoint(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
    }
}
