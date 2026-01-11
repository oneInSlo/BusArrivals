package com.ridango.gtfs;

import java.time.LocalDate;
import java.time.LocalDateTime;

final class GtfsTime {
    private GtfsTime() {}

    static LocalDateTime toDateTime(LocalDate baseDate, String hhmmss) {
        String[] parts = hhmmss.split(":");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid GTFS time: " + hhmmss);

        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int second = (parts.length >= 3) ? Integer.parseInt(parts[2]) : 0;

        int daysToAdd = hour / 24;
        int normalizedHour = hour % 24;

        return baseDate
                .plusDays(daysToAdd)
                .atTime(normalizedHour, minute, second);
    }
}
