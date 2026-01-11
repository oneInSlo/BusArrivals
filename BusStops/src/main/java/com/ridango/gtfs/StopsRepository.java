package com.ridango.gtfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class StopsRepository {
    private final DataSource ds;

    public StopsRepository(DataSource ds) {
        this.ds = ds;
    }

    public Map<Integer, String> loadStopNames() throws IOException {
        try (BufferedReader br = ds.open("stops.txt")) {
            String header = br.readLine();
            if (header == null) return Map.of();

            int stopIdIdx = Csv.headerIndex(header, "stop_id");
            int stopNameIdx = Csv.headerIndex(header, "stop_name");

            Map<Integer, String> map = new HashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = Csv.parseLine(line);
                if (cols.length <= Math.max(stopIdIdx, stopNameIdx)) continue;

                String idRaw = cols[stopIdIdx].trim();
                String name = cols[stopNameIdx].trim();
                if (idRaw.isEmpty() || name.isEmpty()) continue;

                try {
                    map.put(Integer.parseInt(idRaw), name);
                } catch (NumberFormatException ignored) {
                    // skip
                }
            }
            return map;
        }
    }
}
