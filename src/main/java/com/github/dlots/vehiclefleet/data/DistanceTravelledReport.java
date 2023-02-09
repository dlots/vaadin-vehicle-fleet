package com.github.dlots.vehiclefleet.data;

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
            if (periodEnd.isAfter(endTime)) {
                periodEnd = endTime;
            }
            result.add(Pair.of(
                    periodEnd,
                    crmService.getDistanceTravelledKmForVehicleInTimeRange(vehicleId, periodStart, periodEnd).toString()
            ));
            periodStart = periodEnd;
        } while (periodEnd != endTime);
    }
}
