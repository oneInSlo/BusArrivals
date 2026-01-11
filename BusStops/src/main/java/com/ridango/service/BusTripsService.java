package com.ridango.service;

import com.ridango.entities.Arrival;
import com.ridango.entities.RouteArrivals;
import com.ridango.entities.Stop;
import com.ridango.gtfs.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public final class BusTripsService {

    private final StopsRepository stopsRepository;
    private final RoutesRepository routesRepository;
    private final TripsIndex tripsIndex;
    private final StopTimesScanner stopTimesScanner;
    private final CalendarRepository calendarRepository;

    public BusTripsService(DataSource ds) {
        this.stopsRepository = new StopsRepository(ds);
        this.routesRepository = new RoutesRepository(ds);
        this.tripsIndex = new TripsIndex(ds);
        this.stopTimesScanner = new StopTimesScanner(ds);
        this.calendarRepository = new CalendarRepository(ds);
    }

    public QueryResult findUpcomingArrivals(Query query) throws IOException {
        int stopId = query.stationId();
        LocalDateTime now = query.now();

        Map<Integer, String> stopNames = stopsRepository.loadStopNames();
        String stopName = stopNames.get(stopId);
        if (stopName == null) {
            throw new IllegalArgumentException("!! Stop ID not found in stops.txt: " + stopId);
        }

        Map<String, TripsIndex.TripInfo> tripIndex = tripsIndex.loadTripIndex();
        Map<String, String> routeNames = routesRepository.loadRouteNames();

        // setup for calendar filtering
        Set<String> activeServices = calendarRepository.activeServiceIds(LocalDate.from(now));
        boolean useCalendarFilter = !activeServices.isEmpty();

        var hits = stopTimesScanner.scanUpcomingArrivals(stopId, now, query.window());

        Map<String, List<Arrival>> grouped = new HashMap<>();

        for (var hit : hits) {
            TripsIndex.TripInfo info = tripIndex.get(hit.tripId());
            if (info == null) continue;

            // calendar filter
            if (useCalendarFilter && !activeServices.contains(info.serviceId())) continue;

            String routeId = info.routeId();
            String routeName = routeNames.getOrDefault(routeId, routeId);

            grouped.computeIfAbsent(routeId, __ -> new ArrayList<>())
                    .add(new Arrival(routeId, routeName, hit.arrivalTime()));
        }

        List<RouteArrivals> routes = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            String routeId = entry.getKey();
            List<Arrival> arrivals = entry.getValue();

            arrivals.sort(Comparator.comparing(Arrival::arrivalTime));
            if (arrivals.size() > query.maxPerRoute()) {
                arrivals = arrivals.subList(0, query.maxPerRoute());
            }

            String routeName = arrivals.isEmpty() ? routeId : arrivals.get(0).routeName();
            routes.add(new RouteArrivals(routeId, routeName, List.copyOf(arrivals)));
        }

        routes.sort(Comparator.comparing(ra -> ra.arrivals().get(0).arrivalTime()));

        return new QueryResult(new Stop(stopId, stopName), List.copyOf(routes), !useCalendarFilter);
    }
}
