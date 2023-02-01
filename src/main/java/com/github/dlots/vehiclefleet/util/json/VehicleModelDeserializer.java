package com.github.dlots.vehiclefleet.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dlots.vehiclefleet.data.entity.VehicleModel;
import com.github.dlots.vehiclefleet.service.CrmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class VehicleModelDeserializer extends JsonDeserializer<VehicleModel> {
    CrmService crmService;

    @Autowired
    public void setCrmService(CrmService crmService) {
        this.crmService = crmService;
    }

    @Override
    public VehicleModel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return crmService.findVehicleModelById(jsonParser.getLongValue()).orElse(null);
    }
}
