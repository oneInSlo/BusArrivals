package com.ridango.gtfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class CalendarRepository {
    private final DataSource ds;
    private static final DateTimeFormatter GTFS_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    public CalendarRepository(DataSource ds) {
        this.ds = ds;
    }

    private record ServiceCalendar(
            boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun,
            LocalDate start, LocalDate end
    ) {

    }

    public Set<String> activeServiceIds(LocalDate date) throws IOException {
        Map<String, ServiceCalendar> calendars = loadCalendars();
        Set<String> active = new HashSet<>();

        DayOfWeek dow = date.getDayOfWeek();

        for (var e : calendars.entrySet()) {
            String serviceId = e.getKey();
            ServiceCalendar sc = e.getValue();

            if (date.isBefore(sc.start) || date.isAfter(sc.end)) continue;

            boolean runsToday = switch (dow) {
                case MONDAY -> sc.mon;
                case TUESDAY -> sc.tue;
                case WEDNESDAY -> sc.wed;
                case THURSDAY -> sc.thu;
                case FRIDAY -> sc.fri;
                case SATURDAY -> sc.sat;
                case SUNDAY -> sc.sun;
            };

            if (runsToday) active.add(serviceId);
        }

        return active;
    }

    private Map<String, ServiceCalendar> loadCalendars() throws IOException {
        try (BufferedReader br = ds.open("calendar.txt")) {
            String header = br.readLine();
            if (header == null) return Map.of();

            int serviceIdIdx = Csv.headerIndex(header, "service_id");
            int monIdx = Csv.headerIndex(header, "monday");
            int tueIdx = Csv.headerIndex(header, "tuesday");
            int wedIdx = Csv.headerIndex(header, "wednesday");
            int thuIdx = Csv.headerIndex(header, "thursday");
            int friIdx = Csv.headerIndex(header, "friday");
            int satIdx = Csv.headerIndex(header, "saturday");
            int sunIdx = Csv.headerIndex(header, "sunday");
            int startIdx = Csv.headerIndex(header, "start_date");
            int endIdx = Csv.headerIndex(header, "end_date");

            Map<String, ServiceCalendar> map = new HashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] c = Csv.parseLine(line);
                if (c.length <= Math.max(serviceIdIdx, endIdx)) continue;

                String serviceId = c[serviceIdIdx].trim();
                if (serviceId.isEmpty()) continue;

                LocalDate start = LocalDate.parse(c[startIdx].trim(), GTFS_DATE);
                LocalDate end = LocalDate.parse(c[endIdx].trim(), GTFS_DATE);

                ServiceCalendar sc = new ServiceCalendar(
                        "1".equals(c[monIdx].trim()),
                        "1".equals(c[tueIdx].trim()),
                        "1".equals(c[wedIdx].trim()),
                        "1".equals(c[thuIdx].trim()),
                        "1".equals(c[friIdx].trim()),
                        "1".equals(c[satIdx].trim()),
                        "1".equals(c[sunIdx].trim()),
                        start, end
                );

                map.put(serviceId, sc);
            }
            return map;
        }
    }
}
