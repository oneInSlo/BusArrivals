package com.ridango.format;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeFormatterTest {

    private final TimeFormatter tf = new TimeFormatter();

    @Test
    void absoluteFormat_isHHmm() {
        LocalDateTime t = LocalDateTime.of(2026, 1, 10, 9, 5, 59);
        assertEquals("09:05", tf.formatAbsolute(t));
    }

    @Test
    void relativeFormat_minutesOnly() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 10, 10, 0, 0);
        LocalDateTime arrival = now.plusMinutes(7).plusSeconds(30);
        assertEquals("7min", tf.formatRelative(now, arrival));
    }

    @Test
    void relativeFormat_hoursAndMinutes() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 10, 10, 0, 0);
        LocalDateTime arrival = now.plusHours(1).plusMinutes(2);
        assertEquals("1h 02min", tf.formatRelative(now, arrival));
    }

    @Test
    void relativeFormat_exactHour() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 10, 10, 0, 0);
        LocalDateTime arrival = now.plusHours(2);
        assertEquals("2h", tf.formatRelative(now, arrival));
    }
}
