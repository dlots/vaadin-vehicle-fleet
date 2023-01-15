package com.github.dlots.vehiclefleet.views.vehicles;

import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Vehicles | Vehicle fleet")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class VehiclesView extends VerticalLayout {
    private final Grid<Vehicle> grid;

    private final CrmService service;

    private final VehicleEditor editor;

    public VehiclesView(CrmService service, VehicleEditor editor) {
        this.service = service;
        this.editor = editor;

        addClassName("vehicles-view");
        setSizeFull();

        grid = new Grid<>();
        configureVehicleGrid();
        grid.asSingleSelect().addValueChangeListener(e -> editor.editVehicle(e.getValue()));
        updateVehicleList();

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            updateVehicleList();
        });

        add(getToolbar(), grid, editor);
    }

    private void configureVehicleGrid() {
        grid.addClassNames("vehicle-grid");
        grid.setSizeFull();

        grid.addColumn(vehicle -> vehicle.getVehicleModel().toString()).setHeader("Model");
        grid.addColumn(vehicle -> vehicle.getEnterprise().getName()).setHeader("Owner");
        grid.addColumn(Vehicle::getVin).setHeader("VIN");
        grid.addColumn(Vehicle::getPriceUsd).setHeader("Price, USD");
        grid.addColumn(Vehicle::getManufactureYear).setHeader("Year");
        grid.addColumn(Vehicle::getKmDistanceTravelled).setHeader("Distance travelled, km");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        Button addContactButton = new Button("Add vehicle", VaadinIcon.PLUS.create());
        addContactButton.addClickListener(e -> editor.editVehicle(new Vehicle()));

        HorizontalLayout toolbar = new HorizontalLayout(addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateVehicleList() {
        grid.setItems(service.findAllVehicles());
    }
}
