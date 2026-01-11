package com.ridango.gtfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class StopTimesScanner {

    public record StopTimeHit(String tripId, LocalDateTime arrivalTime) {}

    private final DataSource ds;

    public StopTimesScanner(DataSource ds) {
        this.ds = ds;
    }

    public List<StopTimeHit> scanUpcomingArrivals(int stopId, LocalDateTime now, Duration window) throws IOException {
        LocalDateTime end = now.plus(window);
        LocalDate baseDate = now.toLocalDate();

        try (BufferedReader br = ds.open("stop_times.txt")) {
            String header = br.readLine();
            if (header == null) return List.of();

            int tripIdIdx = Csv.headerIndex(header, "trip_id");
            int stopIdIdx = Csv.headerIndex(header, "stop_id");
            int arrivalIdx = Csv.headerIndex(header, "arrival_time");

            List<StopTimeHit> out = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = Csv.parseLine(line);
                if (cols.length <= Math.max(tripIdIdx, Math.max(stopIdIdx, arrivalIdx))) continue;

                String stopRaw = cols[stopIdIdx].trim();
                if (!stopRaw.equals(String.valueOf(stopId))) continue;

                String tripId = cols[tripIdIdx].trim();
                String arrivalRaw = cols[arrivalIdx].trim();
                if (tripId.isEmpty() || arrivalRaw.isEmpty()) continue;

                LocalDateTime arrivalTime = GtfsTime.toDateTime(baseDate, arrivalRaw);


                if (!arrivalTime.isBefore(now) && !arrivalTime.isAfter(end)) {
                    out.add(new StopTimeHit(tripId, arrivalTime));
                }
            }
            return out;
        }
    }
}
