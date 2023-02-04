package com.github.dlots.vehiclefleet.views.vehicles;

import com.github.dlots.vehiclefleet.data.entity.Ride;
import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.data.entity.VehicleModel;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.util.DateTimeUtil;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.util.TimeZone;

@SpringComponent
@UIScope
public class VehicleEditor extends HorizontalLayout implements KeyNotifier {
    private final CrmService crmService;

    private final Binder<Vehicle> binder = new Binder<>(Vehicle.class);

    private ChangeHandler changeHandler;

    private Vehicle vehicle;

    private TimeZone timeZone;

    private final Select<VehicleModel> vehicleModel;
    private final TextField vin;
    private final TextField priceUsd;
    private final TextField manufactureYear;
    private final TextField distanceTravelledKm;

    private final Grid<Ride> rideGrid;
    private final GpsTrackViewer gpsTrackViewer;

    Button saveButton;
    Button deleteButton;

    public VehicleEditor(CrmService crmService) {
        this.crmService = crmService;

        vehicleModel = new Select<>();
        vin = new TextField();
        priceUsd = new TextField();
        manufactureYear = new TextField();
        distanceTravelledKm = new TextField();
        setFieldsProperties();

        saveButton = new Button("Save", VaadinIcon.CHECK.create());
        saveButton.addClickListener(e -> save());
        deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
        deleteButton.addClickListener(e -> delete());
        Button cancel = new Button("Cancel", LumoIcon.UNDO.create());
        cancel.addClickListener(e -> editVehicle(null, false));
        HorizontalLayout actions = new HorizontalLayout(saveButton, deleteButton, cancel);
        VerticalLayout mainLayout = new VerticalLayout(vehicleModel, vin, priceUsd, manufactureYear, distanceTravelledKm, actions);
        binder.bindInstanceFields(this);

        rideGrid = new Grid<>();
        configureRideGrid();
        gpsTrackViewer = new GpsTrackViewer(crmService);
        rideGrid.asSingleSelect().addValueChangeListener(e -> gpsTrackViewer.showGpsTrack(vehicle, e.getValue()));

        add(mainLayout, rideGrid, gpsTrackViewer);
        setVisible(false);
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    private void setFieldsProperties() {
        vehicleModel.setLabel("Vehicle model");
        vehicleModel.setItems(crmService.findAllVehicleModels());
        vehicleModel.setPlaceholder("Select model");

        vin.setLabel("VIN");
        vin.setPlaceholder("Enter VIN");
        vin.setMinLength(17);
        vin.setMaxLength(17);
        vin.setHelperText("Exactly 17 characters");

        priceUsd.setLabel("Price");
        priceUsd.setPlaceholder("Enter price");
        priceUsd.setPrefixComponent(VaadinIcon.DOLLAR.create());
        priceUsd.setHelperText("US dollars");

        manufactureYear.setLabel("Manufacture year");
        manufactureYear.setPlaceholder("Enter manufacture year");
        manufactureYear.setMinLength(4);
        manufactureYear.setMaxLength(4);

        distanceTravelledKm.setLabel("Distance travelled");
        distanceTravelledKm.setPlaceholder("Enter distance travelled");
        distanceTravelledKm.setSuffixComponent(new Span("km"));
    }

    private void configureRideGrid() {
        rideGrid.addClassNames("ride-grid");
        rideGrid.setSizeFull();
        rideGrid.addColumn(ride -> DateTimeUtil.formatInstantToString(ride.getStartTime(), timeZone))
                .setHeader("Ride started (your TZ)");
        rideGrid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void save() {
        crmService.saveVehicle(vehicle);
        changeHandler.onChange();
    }

    private void delete() {
        crmService.deleteVehicle(vehicle);
        changeHandler.onChange();
    }

    public final void editVehicle(Vehicle v, boolean allowEdit) {
        if (v == null) {
            setVisible(false);
            return;
        }
        vehicle = v;
        binder.setBean(vehicle);

        vehicleModel.setReadOnly(!allowEdit);
        vin.setReadOnly(!allowEdit);
        priceUsd.setReadOnly(!allowEdit);
        manufactureYear.setReadOnly(!allowEdit);
        distanceTravelledKm.setReadOnly(!allowEdit);
        saveButton.setVisible(allowEdit);
        deleteButton.setVisible(allowEdit);

        if (!allowEdit) {
            rideGrid.setItems(crmService.findRidesByVehicleId(vehicle.getId()));
        }
        rideGrid.setVisible(!allowEdit);
        gpsTrackViewer.setVisible(false);

        setSizeFull();
        setVisible(true);
    }

    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }
}
