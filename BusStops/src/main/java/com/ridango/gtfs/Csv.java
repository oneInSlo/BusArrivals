package com.ridango.gtfs;

import java.util.Locale;

final class Csv {

    private Csv() {

    }

    static int headerIndex(String headerLine, String columnName) {
        String[] headers = parseLine(headerLine);
        String target = columnName.toLowerCase(Locale.ROOT);

        for (int i = 0; i < headers.length; i++) {
            String h = headers[i];

            if (h == null) continue;

            h = h.trim();
            if (!h.isEmpty() && h.charAt(0) == '\uFEFF') {
                h = h.substring(1);
            }

            if (h.toLowerCase(Locale.ROOT).equals(target)) {
                return i;
            }
        }

        throw new IllegalArgumentException("!! Missing required GTFS column: " + columnName);
    }


    static String[] parseLine(String line) {
        if (line == null) return new String[0];

        var cells = new java.util.ArrayList<String>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                cells.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        cells.add(cur.toString());
        return cells.toArray(new String[0]);
    }
}
