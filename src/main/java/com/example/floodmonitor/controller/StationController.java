package com.example.floodmonitor.controller;

import com.example.floodmonitor.model.*;
import com.example.floodmonitor.service.StationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
public class StationController {

    private final StationService service;

    public StationController(StationService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(service.getAllStations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> getStation(@PathVariable String id) {
        Station station = service.getStation(id);
        if (station == null) throw new RuntimeException("Station " + id + " wurde nicht gefunden");
        return ResponseEntity.ok(station);
    }

    @GetMapping("/{id}/measurements/current")
    public ResponseEntity<Measurement> getCurrentMeasurement(@PathVariable String id) {
        if (service.getStation(id) == null) throw new RuntimeException("Station " + id + " wurde nicht gefunden");
        Measurement m = service.getCurrentMeasurement(id);
        return m != null ? ResponseEntity.ok(m) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/measurements")
    public ResponseEntity<List<Measurement>> getMeasurements(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "100") int limit) {

        if (service.getStation(id) == null) throw new RuntimeException("Station " + id + " wurde nicht gefunden");
        if (from != null && to != null && from.isAfter(to)) throw new IllegalArgumentException("'from' darf nicht nach 'to' liegen.");
        if (limit < 1 || limit > 1000) throw new IllegalArgumentException("Limit muss zwischen 1 und 1000 liegen.");

        return ResponseEntity.ok(service.getMeasurements(id, from, to, limit));
    }

    @GetMapping("/warnings/{warning}")
    public ResponseEntity<List<Measurement>> getMeasurementsByWarning(@PathVariable WarningLevel warning) {
        return ResponseEntity.ok(service.getMeasurementsByWarning(warning));
    }

    @GetMapping("/{id}/measurements/statistics")
    public ResponseEntity<Statistics> getStatistics(@PathVariable String id) {
        if (service.getStation(id) == null) throw new RuntimeException("Station " + id + " wurde nicht gefunden");
        return ResponseEntity.ok(service.getStatistics(id));
    }
}