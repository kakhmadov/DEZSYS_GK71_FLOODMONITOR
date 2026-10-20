package com.example.floodmonitor.repository;

import com.example.floodmonitor.model.Station;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class StationRepository {
    private final Map<String, Station> stations = new HashMap<>();

    public StationRepository() {
        String now = LocalDateTime.now().toString();

        stations.put("ST-001", new Station("ST-001", "Rhein Pegel 1", now, 250.5, 14.3, "cm"));
        stations.put("ST-002", new Station("ST-002", "Donau Pegel 2", now, 320.0, 15.1, "cm"));
        stations.put("ST-003", new Station("ST-003", "Elbe Pegel 3", now, 510.2, 12.8, "cm"));
    }

    public List<Station> findAll() { return new ArrayList<>(stations.values()); }

    public Station findById(String id) { return stations.get(id); }

    public void save(Station station) {
        stations.put(station.getId(), station);
    }
}