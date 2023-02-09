package com.github.dlots.vehiclefleet.data;

import com.github.dlots.vehiclefleet.service.CrmService;
import org.springframework.data.util.Pair;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public interface Report {
    List<Pair<Instant, String>> getResult();

    ReportType getReportType();

    ChronoUnit getPeriodType();

    void setReportResult(List<Pair<Instant, String>> result);

    static DistanceTravelledReport of(Long vehicleId, ReportType reportType, ChronoUnit periodType, Instant startTime, Instant endTime, CrmService crmService) {
        if (!List.of(ChronoUnit.DAYS, ChronoUnit.MONTHS, ChronoUnit.YEARS).contains(periodType)) {
            throw new IllegalArgumentException("Period type not supported");
        }
        if (reportType == ReportType.DISTANCE_TRAVELLED) {
            return new DistanceTravelledReport(vehicleId, periodType, startTime, endTime, crmService);
        }
        throw new IllegalArgumentException("Report type not supported");
    }
}
