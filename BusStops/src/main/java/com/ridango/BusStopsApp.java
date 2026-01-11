package com.ridango;

import com.ridango.cli.ArgsParser;
import com.ridango.format.ConsoleRenderer;
import com.ridango.gtfs.DataSource;
import com.ridango.service.BusTripsService;
import com.ridango.service.Query;
import com.ridango.service.QueryResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;

public final class BusStopsApp {

    public static void main(String[] args) {
        try {
            Clock clock = Clock.systemDefaultZone();
            Query query = ArgsParser.parse(args, clock);

            Path gtfsDir = resolveGtfsDir();
            DataSource ds = new DataSource(gtfsDir);

            BusTripsService service = new BusTripsService(ds);
            QueryResult result = service.findUpcomingArrivals(query);

            new ConsoleRenderer().render(query, result);

        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println(ArgsParser.usage());
            System.exit(2);
        } catch (Exception e) {
            System.err.println("!! Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    static Path resolveGtfsDir() {
        String sysProp = System.getProperty("gtfs.path");
        if (sysProp != null && !sysProp.isBlank()) {
            Path p = Path.of(sysProp.trim());
            if (!Files.isDirectory(p)) throw new IllegalArgumentException("!! gtfs.path is not a directory: " + p);
            return p;
        }

        String env = System.getenv("GTFS_PATH");
        if (env != null && !env.isBlank()) {
            Path p = Path.of(env.trim());
            if (!Files.isDirectory(p)) throw new IllegalArgumentException("!! GTFS_PATH is not a directory: " + p);
            return p;
        }

        Path fallback = Path.of("src", "main", "resources", "gtfs");
        if (!Files.isDirectory(fallback)) {
            throw new IllegalArgumentException(
                    "!! GTFS directory not found."
            );
        }
        return fallback;
    }
}
