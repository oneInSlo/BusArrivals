package com.ridango.gtfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class TripsIndex {
    private final DataSource ds;

    public TripsIndex(DataSource ds) {
        this.ds = ds;
    }

    public record TripInfo(String routeId, String serviceId) {}

    public Map<String, TripInfo> loadTripIndex() throws IOException {
        try (BufferedReader br = ds.open("trips.txt")) {
            String header = br.readLine();
            if (header == null) return Map.of();

            int tripIdIdx = Csv.headerIndex(header, "trip_id");
            int routeIdIdx = Csv.headerIndex(header, "route_id");
            int serviceIdIdx = Csv.headerIndex(header, "service_id");

            Map<String, TripInfo> map = new HashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = Csv.parseLine(line);
                if (cols.length <= Math.max(tripIdIdx, Math.max(routeIdIdx, serviceIdIdx))) continue;

                String tripId = cols[tripIdIdx].trim();
                String routeId = cols[routeIdIdx].trim();
                String serviceId = cols[serviceIdIdx].trim();

                if (!tripId.isEmpty() && !routeId.isEmpty() && !serviceId.isEmpty()) {
                    map.put(tripId, new TripInfo(routeId, serviceId));
                }
            }
            return map;
        }
    }
}
