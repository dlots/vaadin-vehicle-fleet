package com.github.dlots.vehiclefleet.util;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.TimeZone;

public class DateTimeUtil {
    private DateTimeUtil() {}

    public static String formatDateToString(Instant date, @Nullable TimeZone timeZone) {
        if (date == null) return null;
        String format = "MM-dd-yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (timeZone != null && !("".equalsIgnoreCase(timeZone.getID().trim()))) {
            //timeZone = TimeZone.getTimeZone("UTC");
            sdf.setTimeZone(timeZone);
        }
        return sdf.format(date);
    }
}
