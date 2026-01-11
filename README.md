# 🚌 BusStops – Upcoming Bus Arrivals (GTFS)

BusStops is a Java command-line application that reads **GTFS public transport schedule data** and displays the **next buses arriving at a given stop** within the next **two hours**.

The project was built as part of a technical assignment and focuses on:
- working with real-world GTFS feeds,
- efficient file-based data processing,
- clean architecture,
- and automated testing.

---

## Features

- Reads standard **GTFS** files:
  - `stops.txt`
  - `stop_times.txt`
  - `trips.txt`
  - `routes.txt`
  - `calendar.txt`
- Filters arrivals to only those within the **next 2 hours**
- Groups results **per route**
- Limits results to **N arrivals per route**
- Supports two display modes:
  - **absolute** → `18:35`
  - **relative** → `4min`, `1h 02min`
- Uses **calendar.txt** to filter inactive services
- Includes a safe fallback if the calendar is outdated
- Includes **unit tests** and **integration tests**

---

## Command line usage

```bash
    busTrips <station_id> <num_buses_per_line> <relative|absolute>
```

### Examples

```bash
    busTrips 2 3 absolute
    busTrips 3 5 relative
```

Example absolute output:

```bash
    AL Masjid Al-nabawi (Clock Roundabout)
    101: 18:49, 18:50, 18:54
    106: 18:50, 18:51, 19:00
    107: 18:50, 18:51, 18:56
```

Example relative output:

```bash
    Uhud battlefield
    101: 5min, 7min, 17min, 20min, 22min
    105: 9min, 20min, 24min, 35min, 39min
```


---

## GTFS data location

The application needs access to GTFS files.  
You can provide the path in one of the following ways:

### Option 1 – JVM system property

```bash
    java -Dgtfs.path=/path/to/gtfs -jar BusStops.jar 2 3 absolute
```


### Option 2 – Environment variable

```bash
    export GTFS_PATH=/path/to/gtfs
    java -jar BusStops.jar 2 3 absolute
```


### Default
If neither is set, the application falls back to:

```bash
    src/main/resources/gtfs
```

This is useful when running from an IDE.

---

## Build and run

### Build

```bash
    mvn clean package
```

This produces a runnable JAR in:

```bash
    target/BusStops-1.0-SNAPSHOT.jar
```

### Run

```bash
    java -jar target/BusStops-1.0-SNAPSHOT.jar 2 3 absolute
```


---

## Testing

This project includes both **unit tests** and **integration tests**.

- Unit tests verify:
  - CSV parsing
  - time formatting
  - GTFS time handling
  - argument parsing

- Integration tests verify:
  - end-to-end GTFS loading
  - calendar filtering
  - grouping by route
  - time window filtering
  - per-route limits

Run all tests:

```bash
    mvn verify
```


---

## Architecture

The project is structured into clear layers:

```bash
    cli         argument parsing & validation
    gtfs        GTFS file reading and parsing
    service     core business logic
    entities    domain models
    format      console output formatting
```


### Memory-efficient design
The largest GTFS file (`stop_times.txt`) is **never fully loaded into memory**.  
It is processed line-by-line and filtered by:
- stop_id
- time window (now → now + 2 hours)

This keeps memory usage low even for large GTFS feeds.

---

## Calendar handling

The application uses `calendar.txt` to determine which `service_id`s are active on the current date.

Some real GTFS feeds contain **outdated calendar ranges**.  
If no services are active for today, the application:
- prints a warning,
- and continues without calendar filtering.

This avoids returning empty results when the feed data is stale.

---

## AI usage

AI tools were used during development as a **coding assistant** for:
- debugging compilation and runtime errors,
- improving code structure and readability,
- suggesting refactoring and test coverage improvements.

All architectural decisions, data flow, GTFS handling, and final implementation were designed and written by the author.

This reflects typical professional use of AI in modern software development: as a productivity and quality-support tool rather than a replacement for engineering work.

---

## Possible extensions

The current implementation focuses on static GTFS schedules and a single-stop, single-day query.  
The following extensions would make the system significantly more realistic, scalable, and closer to a production-grade transit platform.

---

### 1. **Query multiple stops at once**
Allow querying multiple stops at once:
```bash
    java BusTripsApplication 10,11,12 3 relative
```

**Challenges:**
- Preserving stop identity in the output (clear separation per stop)
- Merging and sorting arrivals across multiple stops
- Avoiding repeated scans of large GTFS files for performance reasons

---

### 2. **GTFS-Realtime integration**
Integrate GTFS-Realtime feeds to reflect live vehicle positions, delays, and cancellations.

**Challenges:**
- Parsing GTFS-Realtime protobuf messages (TripUpdates, VehiclePositions, Alerts)
- Merging static schedule data with real-time delay offsets
- Handling cancelled or missing trips gracefully
- Optionally refreshing output continuously

---

### 3. **Geospatial queries (nearest stops)** 
GTFS feeds define a time zone per agency:

```bash
    java -jar BusStops.jar --lat 24.4714 --lon 39.6111 --radius 500
```

**Challenges:**
- Parsing stop coordinates from stops.txt
- Calculating distances (Haversine formula)
- Finding stops within a given radius
- Aggregating and ranking arrivals from multiple nearby stops

---

### 4. **Web Service / REST API** 
Expose the application as a backend service:

```bash
    GET /api/stops/{stopId}/arrivals?limit=3&format=relative
```

**Challenges:**
- HTTP request handling
- JSON serialization
- Error handling with proper HTTP codes
- Query parameter validation
- Caching for performance

---

### 5. **Accessibility Features** 
Filter by wheelchair accessibility (with additional data):

**Use fields:**
- `wheelchair_boarding` from `stops.txt`
- `wheelchair_accessible` from `trips.txt`

**Challenges:**
- Multi-criteria filtering
- Clearing accessibility status display
- Fallback recommendations
- User preference storage