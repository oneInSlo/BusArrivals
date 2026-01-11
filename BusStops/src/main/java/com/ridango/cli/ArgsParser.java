package com.ridango.cli;

import com.ridango.service.Query;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public final class ArgsParser {

    private ArgsParser() {

    }

    public static String usage() {
        return """
               -- Usage:
                    busTrips <station_id> <num_buses_per_line> <relative|absolute>

               -- Parameters:
                    station_id (int)            ID of the stop (e.g., 2)
                    num_buses_per_line (int)    Max upcoming arrivals shown per route
                    relative|absolute           Time display mode

               -- Examples:
                    busTrips 2 3 absolute
                    busTrips 2 5 relative
               """;
    }

    public static Query parse(String[] args, Clock clock) {
        Objects.requireNonNull(args, "!! args must not be null");
        Objects.requireNonNull(clock, "!! Clock must not be null");

        if (args.length != 3) {
            throw new IllegalArgumentException("!! Expected 3 arguments.\n" + usage());
        }

        int stationId = parsePositiveInt(args[0], "station_id");
        int maxPerRoute = parsePositiveInt(args[1], "num_buses_per_line");
        OutputMode mode = OutputMode.outputFormat(args[2]);

        LocalDateTime now = LocalDateTime.now(clock);
        return new Query(stationId, maxPerRoute, mode, now, Duration.ofHours(2));
    }

    private static int parsePositiveInt(String raw, String name) {
        try {
            int v = Integer.parseInt(raw.trim());
            if (v <= 0) throw new IllegalArgumentException();
            return v;
        } catch (Exception e) {
            throw new IllegalArgumentException("!! Invalid " + name + ": '" + raw + "'. Must be a positive integer.");
        }
    }
}
