package com.ridango.gtfs;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GtfsTimeTest {

    @Test
    void parsesNormalTime() {
        LocalDate base = LocalDate.of(2026, 1, 10);
        LocalDateTime dt = GtfsTime.toDateTime(base, "18:35:00");
        assertEquals(LocalDateTime.of(2026, 1, 10, 18, 35, 0), dt);
    }

    @Test
    void supportsOver24Hours() {
        LocalDate base = LocalDate.of(2026, 1, 10);
        LocalDateTime dt = GtfsTime.toDateTime(base, "25:10:00");
        assertEquals(LocalDateTime.of(2026, 1, 11, 1, 10, 0), dt);
    }

    @Test
    void allowsNoSeconds() {
        LocalDate base = LocalDate.of(2026, 1, 10);
        LocalDateTime dt = GtfsTime.toDateTime(base, "06:05");
        assertEquals(LocalDateTime.of(2026, 1, 10, 6, 5, 0), dt);
    }
}
