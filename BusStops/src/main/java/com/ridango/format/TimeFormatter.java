package com.ridango.format;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeFormatter {

    private static final DateTimeFormatter ABS = DateTimeFormatter.ofPattern("HH:mm");

    public String formatAbsolute(LocalDateTime t) {
        return ABS.format(t);
    }

    public String formatRelative(LocalDateTime now, LocalDateTime arrival) {
        Duration d = Duration.between(now, arrival);
        long totalMin = Math.max(0, d.toMinutes());

        long h = totalMin / 60;
        long m = totalMin % 60;

        if (h == 0) return m + "min";
        if (m == 0) return h + "h";
        return h + "h " + String.format("%02d", m) + "min";
    }
}
