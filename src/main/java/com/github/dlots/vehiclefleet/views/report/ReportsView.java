package com.github.dlots.vehiclefleet.views.report;

import com.github.dlots.vehiclefleet.service.report.Report;
import com.github.dlots.vehiclefleet.service.report.ReportType;
import com.github.dlots.vehiclefleet.data.entity.Enterprise;
import com.github.dlots.vehiclefleet.data.entity.Vehicle;
import com.github.dlots.vehiclefleet.service.CrmService;
import com.github.dlots.vehiclefleet.util.DateTimeUtil;
import com.github.dlots.vehiclefleet.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.util.Pair;

import javax.annotation.security.PermitAll;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimeZone;

@PageTitle("Reports | Vehicle fleet")
@Route(value = "reports", layout = MainLayout.class)
@PermitAll
public class ReportsView extends HorizontalLayout {
    private final CrmService crmService;

    private final Grid<Pair<Instant, String>> reportGrid = new Grid<>();
    private Select<Enterprise> selectEnterprise;
    private Select<Vehicle> selectVehicle;
    private Select<ReportType> selectReportType;
    private Select<ChronoUnit> selectChronoUnit;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    public ReportsView(CrmService crmService) {
        this.crmService = crmService;

        addClassName("reports-view");
        setSizeFull();
        configureReportGrid();
        add(getToolbar(), reportGrid);
        reportGrid.setVisible(false);
    }

    private void configureReportGrid() {
        reportGrid.addClassNames("report-grid");
        reportGrid.setSizeFull();
        reportGrid.addColumn(instantPair -> DateTimeUtil.formatInstantToString(instantPair.getFirst(), TimeZone.getTimeZone("Europe/Moscow")));
        reportGrid.addColumn(Pair::getSecond);
        reportGrid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private VerticalLayout getToolbar() {
        selectVehicle = new Select<>();

        selectEnterprise = new Select<>();
        List<Enterprise> enterprises = crmService.findCurrentManagerEnterprises();
        selectEnterprise.setItems(enterprises);
        selectEnterprise.addValueChangeListener(e -> {
            List<Vehicle> vehicles = crmService.findVehiclesForEnterprise(selectEnterprise.getValue().getId());
            selectVehicle.setItems(vehicles);
            if (!vehicles.isEmpty()) {
                selectVehicle.setValue(vehicles.get(0));
            }
        });
        if (enterprises != null && !enterprises.isEmpty()) {
            selectEnterprise.setValue(enterprises.get(0));
        }

        selectReportType = new Select<>();
        selectReportType.setItems(ReportType.values());
        selectReportType.setPlaceholder("Select report type");

        selectChronoUnit = new Select<>();
        selectChronoUnit.setItems(List.of(ChronoUnit.DAYS, ChronoUnit.MONTHS, ChronoUnit.YEARS));
        selectChronoUnit.setPlaceholder("Select time unit:");

        startDatePicker = new DatePicker("Start date:");
        endDatePicker = new DatePicker("End date:");


        Button generateReport = new Button("Generate report");
        generateReport.addClickListener(e -> generateReport());

        return new VerticalLayout(
                selectEnterprise, selectVehicle, selectReportType, selectChronoUnit, startDatePicker, endDatePicker,
                generateReport);
    }

    private void generateReport() {
        Report report = crmService.getReportForVehicleByChronoUnitInDateRange(
                selectVehicle.getValue().getId(),
                selectReportType.getValue(),
                selectChronoUnit.getValue(),
                startDatePicker.getValue().atStartOfDay(),
                endDatePicker.getValue().atStartOfDay()
        );
        reportGrid.getColumns().get(0).setHeader(
                String.format("Start of period date (by %s)", report.getPeriodType().toString())
        );
        reportGrid.getColumns().get(1).setHeader(report.getReportType().toString());
        reportGrid.setItems(report.getResult());
        reportGrid.setVisible(true);
    }
}
