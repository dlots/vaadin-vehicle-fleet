package com.github.dlots.vehiclefleet.service.report;

import com.github.dlots.vehiclefleet.service.CrmService;
import org.springframework.data.util.Pair;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public abstract class AbstractReport implements Report {
    public AbstractReport(Long vehicleId, ReportType reportType, ChronoUnit periodType,
                          Instant startTime, Instant endTime, CrmService crmService) {
        this.vehicleId = vehicleId;
        this.reportType = reportType;
        this.periodType = periodType;
        this.startTime = startTime;
        this.endTime = endTime;
        makeResult(crmService);
    }

    protected final Long vehicleId;
    protected final ReportType reportType;
    protected final ChronoUnit periodType;
    protected final Instant startTime;
    protected final Instant endTime;
    protected List<Pair<Instant, String>> result;

    public List<Pair<Instant, String>> getResult() {
        return result;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public ChronoUnit getPeriodType() {
        return periodType;
    }

    protected abstract void makeResult(CrmService crmService);

    protected Instant nextPeriodEnd(Instant currentPeriodEnd) {
        long amountToAdd = 0;
        switch (periodType) {
            case DAYS:
                amountToAdd = 1;
                break;
            case MONTHS:
                amountToAdd = 30;
                break;
            case YEARS:
                amountToAdd = 365;
        }
        return currentPeriodEnd.plus(amountToAdd, ChronoUnit.DAYS);
    }
}
