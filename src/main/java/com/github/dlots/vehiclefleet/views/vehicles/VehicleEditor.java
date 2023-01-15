package com.github.dlots.vehiclefleet.views.vehicles;

import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.data.entity.VehicleModel;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
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

@SpringComponent
@UIScope
public class VehicleEditor extends VerticalLayout implements KeyNotifier {
    private final CrmService service;

    private final Binder<Vehicle> binder = new Binder<>(Vehicle.class);

    private ChangeHandler changeHandler;

    private Vehicle vehicle;

    private final Select<VehicleModel> vehicleModel;
    private final TextField vin;
    private final TextField priceUsd;
    private final TextField manufactureYear;
    private final TextField kmDistanceTravelled;

    public VehicleEditor(CrmService service) {
        this.service = service;

        vehicleModel = new Select<>();
        vin = new TextField();
        priceUsd = new TextField();
        manufactureYear = new TextField();
        kmDistanceTravelled = new TextField();
        setFieldsProperties();

        Button save = new Button("Save", VaadinIcon.CHECK.create());
        save.addClickListener(e -> save());
        Button delete = new Button("Delete", VaadinIcon.TRASH.create());
        delete.addClickListener(e -> delete());
        Button cancel = new Button("Cancel", LumoIcon.UNDO.create());
        cancel.addClickListener(e -> editVehicle(null));
        HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);

        add(vehicleModel, vin, priceUsd, manufactureYear, kmDistanceTravelled, actions);

        binder.bindInstanceFields(this);

        setVisible(false);
    }

    private void setFieldsProperties() {
        vehicleModel.setLabel("Vehicle model");
        vehicleModel.setItems(service.findAllVehicleModels());
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

        kmDistanceTravelled.setLabel("Distance travelled");
        kmDistanceTravelled.setPlaceholder("Enter distance travelled");
        kmDistanceTravelled.setSuffixComponent(new Span("km"));
    }

    private void save() {
        service.saveVehicle(vehicle);
        changeHandler.onChange();
    }

    private void delete() {
        service.deleteVehicle(vehicle);
        changeHandler.onChange();
    }

    public final void editVehicle(Vehicle v) {
        if (v == null) {
            setVisible(false);
            return;
        }
        if (v.getId() != null) {
            //noinspection OptionalGetWithoutIsPresent
            vehicle = service.findVehicleById(v.getId()).get();
        } else {
            vehicle = v;
        }
        binder.setBean(vehicle);
        setVisible(true);
    }

    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }
}
