package com.ridango.gtfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class RoutesRepository {
    private final DataSource ds;

    public RoutesRepository(DataSource ds) {
        this.ds = ds;
    }

    public Map<String, String> loadRouteNames() throws IOException {
        try (BufferedReader br = ds.open("routes.txt")) {
            String header = br.readLine();
            if (header == null) return Map.of();

            int routeIdIdx = Csv.headerIndex(header, "route_id");
            int shortNameIdx = Csv.headerIndex(header, "route_short_name");
            int longNameIdx = Csv.headerIndex(header, "route_long_name");

            Map<String, String> map = new HashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = Csv.parseLine(line);
                if (cols.length <= Math.max(routeIdIdx, Math.max(shortNameIdx, longNameIdx))) continue;

                String routeId = cols[routeIdIdx].trim();
                if (routeId.isEmpty()) continue;

                String shortName = cols[shortNameIdx].trim();
                String longName = cols[longNameIdx].trim();
                String chosen = !shortName.isEmpty() ? shortName : longName;
                if (chosen.isEmpty()) chosen = routeId;

                map.put(routeId, chosen);
            }
            return map;
        }
    }
}
