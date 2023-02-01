package com.github.dlots.vehiclefleet.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dlots.vehiclefleet.data.entity.Driver;
import com.github.dlots.vehiclefleet.service.CrmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DriversDeserializer extends JsonDeserializer<List<Driver>> {
    CrmService crmService;

    @Autowired
    public void setCrmService(CrmService crmService) {
        this.crmService = crmService;
    }

    @Override
    public List<Driver> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        return null;
    }
}
