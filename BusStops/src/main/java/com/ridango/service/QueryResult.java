package com.ridango.service;

import com.ridango.entities.RouteArrivals;
import com.ridango.entities.Stop;

import java.util.List;

public record QueryResult(
        Stop stop,
        List<RouteArrivals> routes,
        boolean calendarIgnored
) {

}
