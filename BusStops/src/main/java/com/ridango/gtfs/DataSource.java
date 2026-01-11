package com.ridango.gtfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public record DataSource(Path gtfsDir) {

    public BufferedReader open(String fileName) throws IOException {
        Path p = gtfsDir.resolve(fileName);
        return Files.newBufferedReader(p, StandardCharsets.UTF_8);
    }
}
