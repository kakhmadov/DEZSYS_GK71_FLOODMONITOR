package com.example.floodmonitor.repository;

import com.example.floodmonitor.model.Measurement;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MeasurementRepository {
    private final List<Measurement> measurements = new ArrayList<>();

    public void save(Measurement m) { measurements.add(m); }

    public List<Measurement> findAll() { return measurements; }

    public List<Measurement> findByStationId(String stationId) {
        return measurements.stream().filter(m -> m.getStationId().equals(stationId)).collect(Collectors.toList());
    }

    public Measurement findLatestByStationId(String stationId) {
        return measurements.stream()
                .filter(m -> m.getStationId().equals(stationId))
                .max(Comparator.comparing(Measurement::getTimestamp))
                .orElse(null);
    }
}