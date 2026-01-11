package com.ridango.format;

import com.ridango.cli.OutputMode;
import com.ridango.entities.Arrival;
import com.ridango.entities.RouteArrivals;
import com.ridango.service.Query;
import com.ridango.service.QueryResult;

import java.util.stream.Collectors;

public final class ConsoleRenderer {

    private final TimeFormatter tf = new TimeFormatter();

    public void render(Query query, QueryResult result) {
        if (result.calendarIgnored()) {
            System.out.println("!!! WARNING: calendar.txt has no active services for today; calendar filtering skipped.");
        }

        System.out.println(result.stop().name());

        for (RouteArrivals ra : result.routes()) {
            String times = ra.arrivals().stream().map(a -> format(query, a)).collect(Collectors.joining(", "));
            System.out.println(ra.routeName() + ": " + times);
        }
    }

    private String format(Query query, Arrival a) {
        return query.mode() == OutputMode.ABSOLUTE ? tf.formatAbsolute(a.arrivalTime()) : tf.formatRelative(query.now(), a.arrivalTime());
    }
}
