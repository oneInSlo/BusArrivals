package com.ridango.entities;

import java.time.LocalDateTime;

public record Arrival(String routeId, String routeName, LocalDateTime arrivalTime)  {

}
