package com.ridango.cli;

import com.ridango.service.Query;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class ArgsParserTest {

    private static Clock fixedClock() {
        return Clock.fixed(Instant.parse("2026-01-10T10:00:00Z"), ZoneId.of("UTC"));
    }

    @Test
    void parsesValidArgs() {
        Query q = ArgsParser.parse(new String[]{"2", "3", "absolute"}, fixedClock());
        assertEquals(2, q.stationId());
        assertEquals(3, q.maxPerRoute());
        assertEquals(OutputMode.ABSOLUTE, q.mode());
        assertEquals(2, q.window().toHours());
    }

    @Test
    void rejectsInvalidMode() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ArgsParser.parse(new String[]{"2", "3", "abs"}, fixedClock()));
        assertTrue(ex.getMessage().contains("Invalid output mode"));
    }

    @Test
    void rejectsNonPositiveIntegers() {
        assertThrows(IllegalArgumentException.class,
                () -> ArgsParser.parse(new String[]{"0", "3", "absolute"}, fixedClock()));
        assertThrows(IllegalArgumentException.class,
                () -> ArgsParser.parse(new String[]{"2", "-1", "absolute"}, fixedClock()));
    }
}
