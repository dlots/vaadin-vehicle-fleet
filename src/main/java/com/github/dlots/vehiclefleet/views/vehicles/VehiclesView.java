package com.github.dlots.vehiclefleet.views.vehicles;

import com.github.dlots.vehiclefleet.data.entity.Enterprise;
import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.util.DateTimeUtil;
import com.github.dlots.vehiclefleet.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@PageTitle("Vehicles | Vehicle fleet")
@Route(value = "vehicles", layout = MainLayout.class)
@PermitAll
public class VehiclesView extends VerticalLayout implements HasUrlParameter<Long> {
    private final Grid<Vehicle> vehicleGrid;

    private Vehicle selectedVehicle;

    private GridListDataView<Vehicle> gridListDataView;

    private final Select<Enterprise> enterpriseSelect;

    private final CrmService crmService;

    private final VehicleEditor editor;

    public VehiclesView(CrmService crmService, VehicleEditor editor) {
        this.crmService = crmService;
        this.editor = editor;

        addClassName("vehicles-view");
        setSizeFull();

        vehicleGrid = new Grid<>();
        configureVehicleGrid();
        vehicleGrid.asSingleSelect().addValueChangeListener(e -> selectedVehicle = e.getValue());
        updateVehicleList();

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            updateVehicleList();
        });

        this.enterpriseSelect = new Select<>();
        add(getToolbar(), vehicleGrid, editor);
    }

    private void configureVehicleGrid() {
        vehicleGrid.addClassNames("vehicle-grid");
        vehicleGrid.setSizeFull();

        vehicleGrid.addColumn(vehicle -> vehicle.getVehicleModel().toString()).setHeader("Model");
        vehicleGrid.addColumn(Vehicle::getVin).setHeader("VIN");
        vehicleGrid.addColumn(Vehicle::getPriceUsd).setHeader("Price, USD");
        vehicleGrid.addColumn(Vehicle::getManufactureYear).setHeader("Year");
        vehicleGrid.addColumn(Vehicle::getDistanceTravelledKm).setHeader("Distance travelled, km");
        Grid.Column<Vehicle> column = vehicleGrid.addColumn(v -> DateTimeUtil.formatInstantToString(
                        v.getPurchaseDateTimeUtc(),
                        v.getEnterprise().getTimeZone()))
                .setHeader("Purchased (enterprise TZ)");
        UI.getCurrent().getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
            vehicleGrid.removeColumn(column);
            vehicleGrid.addColumn(v -> {
                TimeZone clientTimeZone = TimeZone.getTimeZone(extendedClientDetails.getTimeZoneId());
                editor.setTimeZone(clientTimeZone);
                return DateTimeUtil.formatInstantToString(
                        v.getPurchaseDateTimeUtc(),
                        clientTimeZone);
            }).setHeader("Purchased (your TZ)");
            vehicleGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        });
        vehicleGrid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        enterpriseSelect.setLabel("Select enterprise");
        List<Enterprise> managedEnterprises = crmService.findCurrentManagerEnterprises();
        enterpriseSelect.setItems(managedEnterprises);
        enterpriseSelect.addValueChangeListener(event -> gridListDataView.refreshAll());
        if (!managedEnterprises.isEmpty()) {
            enterpriseSelect.setValue(managedEnterprises.get(0));
        }

        Button addContactButton = new Button("Add vehicle", VaadinIcon.PLUS.create());
        addContactButton.addClickListener(e -> editor.editVehicle(new Vehicle(), true));
        Button viewSelectedButton = new Button("View selected", VaadinIcon.EYE.create());
        viewSelectedButton.addClickListener(e -> editor.editVehicle(selectedVehicle, false));
        Button editSelectedButton = new Button("Edit selected", VaadinIcon.PENCIL.create());
        editSelectedButton.addClickListener(e -> editor.editVehicle(selectedVehicle, true));

        HorizontalLayout toolbar = new HorizontalLayout(
                enterpriseSelect, addContactButton, viewSelectedButton, editSelectedButton);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(Alignment.END);
        return toolbar;
    }

    private void updateVehicleList() {
        gridListDataView = vehicleGrid.setItems(crmService.findAllVehiclesForManagedEnterprises());
        gridListDataView.addFilter(vehicle -> vehicle.getEnterprise().getId().equals(enterpriseSelect.getValue().getId()));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long parameter) {
        List<String> ids = beforeEvent.getLocation().getQueryParameters().getParameters().get("enterprise_id");
        if (ids != null && !ids.isEmpty() && !enterpriseSelect.isEmpty()) {
            Long enterpriseId = Long.parseLong(ids.get(0));
            Optional<Enterprise> enterprise = crmService.findEnterpriseById(enterpriseId);
            enterprise.ifPresent(enterpriseSelect::setValue);
        }
    }
}
