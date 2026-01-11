package com.ridango.integration;

import com.ridango.cli.OutputMode;
import com.ridango.entities.RouteArrivals;
import com.ridango.gtfs.DataSource;
import com.ridango.service.BusTripsService;
import com.ridango.service.Query;
import com.ridango.service.QueryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BusTripsServiceIT {

    @TempDir
    Path tmp;

    @Test
    void endToEnd_filtersGroupsAndLimits() throws IOException {
        // Fixed "now"
        LocalDateTime now = LocalDateTime.of(2026, 1, 10, 10, 0, 0);

        // Minimal GTFS files
        write(tmp.resolve("stops.txt"), """
                stop_id,stop_name
                2,Test Stop
                """);

        write(tmp.resolve("routes.txt"), """
                route_id,route_short_name,route_long_name
                R1,101,Route 101
                R2,106,Route 106
                """);

        write(tmp.resolve("trips.txt"), """
                route_id,service_id,trip_id
                R1,SVC,TRIP_A
                R1,SVC,TRIP_B
                R1,SVC,TRIP_C
                R2,SVC,TRIP_D
                """);

        // stop_times: three trips on R1 within window; one outside window; one on R2 within
        // We keep stop_times small but representative.
        write(tmp.resolve("stop_times.txt"), """
                trip_id,arrival_time,departure_time,stop_id,stop_sequence
                TRIP_A,10:05:00,10:05:00,2,1
                TRIP_B,10:20:00,10:20:00,2,1
                TRIP_C,11:30:00,11:30:00,2,1
                TRIP_D,10:10:00,10:10:00,2,1
                TRIP_OUT,13:30:00,13:30:00,2,1
                """);

        write(tmp.resolve("calendar.txt"), """
                service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
                SVC,1,1,1,1,1,1,1,20260101,20261231
                """);

        DataSource ds = new DataSource(tmp);
        BusTripsService service = new BusTripsService(ds);

        Query q = new Query(2, 2, OutputMode.ABSOLUTE, now, Duration.ofHours(2));
        QueryResult result = service.findUpcomingArrivals(q);

        assertEquals("Test Stop", result.stop().name());

        // We expect 2 routes: R1 and R2
        List<RouteArrivals> routes = result.routes();
        assertEquals(2, routes.size());

        // R1 should be limited to 2 arrivals (because maxPerRoute=2)
        RouteArrivals r1 = routes.stream().filter(r -> r.routeId().equals("R1")).findFirst().orElseThrow();
        assertEquals(2, r1.arrivals().size());
        assertEquals(LocalDateTime.of(2026, 1, 10, 10, 5, 0), r1.arrivals().get(0).arrivalTime());
        assertEquals(LocalDateTime.of(2026, 1, 10, 10, 20, 0), r1.arrivals().get(1).arrivalTime());

        // R2 should have 1 arrival
        RouteArrivals r2 = routes.stream().filter(r -> r.routeId().equals("R2")).findFirst().orElseThrow();
        assertEquals(1, r2.arrivals().size());
        assertEquals(LocalDateTime.of(2026, 1, 10, 10, 10, 0), r2.arrivals().get(0).arrivalTime());

        // Ensure out-of-window trip is excluded (13:30 is 3.5h after 10:00)
        assertTrue(routes.stream().flatMap(r -> r.arrivals().stream()).noneMatch(a -> a.arrivalTime().getHour() == 13));
    }

    private static void write(Path path, String content) throws IOException {
        Files.writeString(path, content.replace("\r\n", "\n"));
    }
}
