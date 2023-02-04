package com.github.dlots.vehiclefleet.views.enterprises;

import com.github.dlots.vehiclefleet.data.entity.Enterprise;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PageTitle("Enterprises | Vehicle fleet")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class EnterprisesView extends VerticalLayout {
    private final Grid<Enterprise> enterpriseGrid = new Grid<>(Enterprise.class, false);

    private final CrmService crmService;

    public EnterprisesView(CrmService crmService) {
        this.crmService = crmService;

        addClassName("enterprises-view");
        setSizeFull();
        configureEnterpriseGrid();
        add(enterpriseGrid);

        updateEnterpriseList();
    }

    private void configureEnterpriseGrid() {
        enterpriseGrid.addClassNames("enterprise-grid");
        enterpriseGrid.setSizeFull();
        enterpriseGrid.addColumn(Enterprise::getName).setHeader("Name");
        enterpriseGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        enterpriseGrid.asSingleSelect().addValueChangeListener(e -> {
            UI.getCurrent().navigate(String.format("vehicles?enterprise_id=%s", e.getValue().getId()));
        });
    }

    private void updateEnterpriseList() {
        enterpriseGrid.setItems(crmService.findCurrentManagerEnterprises());
    }
}
