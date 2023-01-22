package com.github.dlots.vehiclefleet.views.enterprises;

import com.github.dlots.vehiclefleet.data.entity.Enterprise;
import com.github.dlots.vehiclefleet.service.ManagerService;
import com.github.dlots.vehiclefleet.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PageTitle("Enterprises | Vehicle fleet")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class EnterprisesView extends VerticalLayout {
    Grid<Enterprise> enterpriseGrid = new Grid<>(Enterprise.class, false);

    private final ManagerService managerService;

    public EnterprisesView(ManagerService managerService) {
        this.managerService = managerService;

        addClassName("enterprises-view");
        setSizeFull();
        configureEnterpriseGrid();
        add(/*getToolbar(),*/ enterpriseGrid);

        updateEnterpriseList();
    }

    private void configureEnterpriseGrid() {
        enterpriseGrid.addClassNames("enterprise-grid");
        enterpriseGrid.setSizeFull();

        enterpriseGrid.addColumn(Enterprise::getName).setHeader("Name");

        enterpriseGrid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        Button addContactButton = new Button("Add enterprise");

        HorizontalLayout toolbar = new HorizontalLayout(addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateEnterpriseList() {
        enterpriseGrid.setItems(managerService.getManagedEnterprises());
    }
}
