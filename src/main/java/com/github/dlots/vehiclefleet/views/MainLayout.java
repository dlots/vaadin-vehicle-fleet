package com.github.dlots.vehiclefleet.views;

import com.github.dlots.vehiclefleet.service.ManagerService;
import com.github.dlots.vehiclefleet.views.enterprises.EnterprisesView;
import com.github.dlots.vehiclefleet.views.report.ReportsView;
import com.github.dlots.vehiclefleet.views.vehiclemodels.VehicleModelsView;
import com.github.dlots.vehiclefleet.views.vehicles.VehiclesView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {
    private final ManagerService managerService;

    public MainLayout(ManagerService managerService) {
        this.managerService = managerService;

        addDrawerContent();
        addHeaderContent();
    }

    private void addDrawerContent() {
        addToDrawer(createLinks());
    }

    private VerticalLayout createLinks() {
        RouterLink enterprisesLink = new RouterLink("Enterprises", EnterprisesView.class);
        enterprisesLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink vehiclesLink = new RouterLink("Vehicles", VehiclesView.class);
        vehiclesLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink modelsLink = new RouterLink("Vehicle models", VehicleModelsView.class);
        modelsLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink reportsLink = new RouterLink("Reports", ReportsView.class);
        modelsLink.setHighlightCondition(HighlightConditions.sameLocation());

        return new VerticalLayout(enterprisesLink, vehiclesLink, modelsLink, reportsLink);
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        H1 appName = new H1("Vehicle fleet");
        appName.addClassNames("text-l", "m-m");

        HorizontalLayout header = new HorizontalLayout(toggle, appName);
        Button loginLogout;
        if (managerService.getAuthenticatedManager() != null) {
            loginLogout = new Button("Logout", buttonClickEvent -> managerService.logout());

        } else {
            loginLogout = new Button("Login", buttonClickEvent -> UI.getCurrent().getPage().setLocation("/login"));
            loginLogout.getElement().getStyle().set("margin-left", "auto");
            header.add(loginLogout);
        }
        loginLogout.getElement().getStyle().set("margin-left", "auto");
        header.add(loginLogout);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }
}
