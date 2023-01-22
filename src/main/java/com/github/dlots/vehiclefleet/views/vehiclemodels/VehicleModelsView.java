package com.github.dlots.vehiclefleet.views.vehiclemodels;

import com.github.dlots.vehiclefleet.data.entity.VehicleModel;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PageTitle("Vehicle models | Vehicle fleet")
@Route(value = "models", layout = MainLayout.class)
@PermitAll
public class VehicleModelsView extends VerticalLayout {
    Grid<VehicleModel> vehicleModelGrid = new Grid<>(VehicleModel.class, false);

    private final CrmService service;

    public VehicleModelsView(CrmService service) {
        this.service = service;

        addClassName("vehicle-models-view");
        setSizeFull();
        configureVehicleModelGrid();
        add(/*getToolbar(),*/ vehicleModelGrid);

        updateVehicleModelList();
    }

    private void configureVehicleModelGrid() {
        vehicleModelGrid.addClassNames("vehicle-model-grid");
        vehicleModelGrid.setSizeFull();

        vehicleModelGrid.addColumn(VehicleModel::getBrandName).setHeader("Brand");
        vehicleModelGrid.addColumn(VehicleModel::getModelName).setHeader("Model");
        vehicleModelGrid.addColumn(VehicleModel::getVehicleType).setHeader("Type");
        vehicleModelGrid.addColumn(VehicleModel::getGasTankVolumeL).setHeader("Gas tank volume, L");
        vehicleModelGrid.addColumn(VehicleModel::getPayloadCapacityKg).setHeader("Payload capacity, kg");
        vehicleModelGrid.addColumn(VehicleModel::getSeatingCapacity).setHeader("Seating capacity");

        vehicleModelGrid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        Button addContactButton = new Button("Add vehicle model");

        HorizontalLayout toolbar = new HorizontalLayout(addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateVehicleModelList() {
        vehicleModelGrid.setItems(service.findAllVehicleModels());
    }
}
