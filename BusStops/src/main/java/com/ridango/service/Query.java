package com.ridango.service;

import com.ridango.cli.OutputMode;

import java.time.Duration;
import java.time.LocalDateTime;

public record Query(
        int stationId,
        int maxPerRoute,
        OutputMode mode,
        LocalDateTime now,
        Duration window
) {

}
