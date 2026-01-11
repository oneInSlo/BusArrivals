package com.ridango.entities;

import java.util.List;

public record RouteArrivals(String routeId, String routeName, List<Arrival> arrivals) {

}
