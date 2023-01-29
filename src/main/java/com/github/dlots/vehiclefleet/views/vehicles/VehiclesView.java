package com.github.dlots.vehiclefleet.views.vehicles;

import com.github.dlots.vehiclefleet.data.entity.Enterprise;
import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.service.ManagerService;
import com.github.dlots.vehiclefleet.util.DateTimeUtil;
import com.github.dlots.vehiclefleet.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.util.List;

@PageTitle("Vehicles | Vehicle fleet")
@Route(value = "vehicles", layout = MainLayout.class)
@PermitAll
public class VehiclesView extends VerticalLayout {
    private final Grid<Vehicle> grid;

    private GridListDataView<Vehicle> gridListDataView;

    private final Select<Enterprise> enterpriseSelect;

    private final CrmService crmService;

    private final ManagerService managerService;

    private final VehicleEditor editor;

    public VehiclesView(CrmService crmService, ManagerService managerService, VehicleEditor editor) {
        this.crmService = crmService;
        this.managerService = managerService;
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

        this.enterpriseSelect = new Select<>();
        add(getToolbar(), grid, editor);
    }

    private void configureVehicleGrid() {
        grid.addClassNames("vehicle-grid");
        grid.setSizeFull();

        grid.addColumn(vehicle -> vehicle.getVehicleModel().toString()).setHeader("Model");
        grid.addColumn(Vehicle::getVin).setHeader("VIN");
        grid.addColumn(Vehicle::getPriceUsd).setHeader("Price, USD");
        grid.addColumn(Vehicle::getManufactureYear).setHeader("Year");
        grid.addColumn(Vehicle::getDistanceTravelledKm).setHeader("Distance travelled, km");
        grid.addColumn(v -> DateTimeUtil.formatDateToString(v.getPurchaseDateTimeUtc(), null)).setHeader("Date and time of purchase");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        enterpriseSelect.setLabel("Select enterprise");
        List<Enterprise> managedEnterprises = managerService.getManagedEnterprises();
        enterpriseSelect.setItems(managedEnterprises);
        enterpriseSelect.addValueChangeListener(event -> gridListDataView.refreshAll());
        enterpriseSelect.setValue(managedEnterprises.get(0));

        Button addContactButton = new Button("Add vehicle", VaadinIcon.PLUS.create());
        addContactButton.addClickListener(e -> editor.editVehicle(new Vehicle()));

        HorizontalLayout toolbar = new HorizontalLayout(enterpriseSelect, addContactButton);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(Alignment.END);
        return toolbar;
    }

    private void updateVehicleList() {
        gridListDataView = grid.setItems(crmService.findAllVehiclesForManagedEnterprises());
        gridListDataView.addFilter(vehicle -> vehicle.getEnterprise().getId().equals(enterpriseSelect.getValue().getId()));
    }
}
