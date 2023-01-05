package com.github.dlots.vehiclefleet.views;

import com.github.dlots.vehiclefleet.data.entity.VehicleModel;
import com.github.dlots.vehiclefleet.data.entity.VehicleType;
import com.github.dlots.vehiclefleet.data.service.CrmService;
import com.github.dlots.vehiclefleet.util.EntityUtil;
import com.github.dlots.vehiclefleet.views.vehiclemodels.VehicleModelsView;
import com.github.dlots.vehiclefleet.views.vehicles.VehiclesView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {
    private static boolean dataIsInitialized = false;

    private final CrmService service;

    public MainLayout(CrmService service) {
        this.service = service;
        debugCreateInitialEntities();

        //setPrimarySection(AppLayout.Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        H1 appName = new H1("Vehicle fleet");
        appName.addClassNames("text-l", "m-m");

        HorizontalLayout header = new HorizontalLayout(toggle, appName);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void addDrawerContent() {
        addToDrawer(createLinks());
    }

    private VerticalLayout createLinks() {
        RouterLink vehiclesLink = new RouterLink("Vehicles", VehiclesView.class);
        vehiclesLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink modelsLink = new RouterLink("Vehicle models", VehicleModelsView.class);
        modelsLink.setHighlightCondition(HighlightConditions.sameLocation());

        return new VerticalLayout(vehiclesLink, modelsLink);
    }

    private Footer createFooter() {
        return new Footer();
    }

    private void debugCreateInitialEntities() {
        if (dataIsInitialized) {
            return;
        }
        dataIsInitialized = true;

        // 1C3BC55D0CG133270 1982 Chrysler LeBaron
        VehicleModel chrysler = EntityUtil.createAndSaveVehicleModel(service, "Chrysler", "LeBaron", VehicleType.CAR, 50, 500, 5);
        EntityUtil.createAndSaveVehicle(service, chrysler, "1C3BC55D0CG133270", 2000, 1982, 100000);

        // 1C3BC55D0CG133270 1982 Chrysler LeBaron
        VehicleModel bike = EntityUtil.createAndSaveVehicleModel(service, "Harley Davidson", "Flht", VehicleType.MOTORCYCLE, 18, 100, 1);
        EntityUtil.createAndSaveVehicle(service, bike, "1HD1DJL22HY504007", 3500, 1987, 30000);
    }
}
