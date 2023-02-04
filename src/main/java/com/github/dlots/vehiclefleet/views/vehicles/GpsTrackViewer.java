package com.github.dlots.vehiclefleet.views.vehicles;

import com.github.dlots.vehiclefleet.data.entity.GpsPoint;
import com.github.dlots.vehiclefleet.data.entity.Ride;
import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.util.YandexMapsHandler;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.List;

@SpringComponent
@UIScope
public class GpsTrackViewer extends VerticalLayout {
    private static final YandexMapsHandler YANDEX_MAPS_HANDLER = new YandexMapsHandler();

    private final CrmService crmService;

    private final Image gpsTrackMap;

    public GpsTrackViewer(CrmService crmService) {
        this.crmService = crmService;
        gpsTrackMap = new Image();
        add(gpsTrackMap);
        setVisible(false);
    }

    public final void showGpsTrack(Vehicle vehicle, Ride ride) {
        if (vehicle == null || ride == null) {
            setVisible(false);
            return;
        }
        List<GpsPoint> gpsPoints = crmService.findGpsPointsForVehicleInUtcRange(
                vehicle.getId(), ride.getStartTime(), ride.getEndTime());
        gpsTrackMap.setSrc(YANDEX_MAPS_HANDLER.getGpsTrackMapUrl(gpsPoints));
        gpsTrackMap.setSizeFull();
        setVisible(true);
    }
}
