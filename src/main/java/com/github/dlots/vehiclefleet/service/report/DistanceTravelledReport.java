package com.github.dlots.vehiclefleet.service.report;

import com.github.dlots.vehiclefleet.service.CrmService;
import org.springframework.data.util.Pair;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class DistanceTravelledReport extends AbstractReport implements Report {
    public DistanceTravelledReport(Long vehicleId, ChronoUnit periodType, Instant startTime, Instant endTime, CrmService crmService) {
        super(vehicleId, ReportType.DISTANCE_TRAVELLED, periodType, startTime, endTime, crmService);
    }

    @Override
    protected void makeResult(CrmService crmService) {
        result = new ArrayList<>();
        Instant periodStart = startTime;
        Instant periodEnd;
        do {
            periodEnd = nextPeriodEnd(periodStart);
            double distanceTravelled = crmService.getDistanceTravelledKmForVehicleInTimeRange(vehicleId, periodStart, periodEnd);
            result.add(Pair.of(periodStart, String.format("%.3f", distanceTravelled)));
            periodStart = periodEnd;
        } while (periodEnd.isBefore(endTime));
    }
}
