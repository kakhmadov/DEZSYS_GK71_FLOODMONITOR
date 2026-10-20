package com.example.floodmonitor.service;

import com.example.floodmonitor.model.*;
import com.example.floodmonitor.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepo;
    private final MeasurementRepository measurementRepo;

    private static final double WARN_LEVEL = 300.0;
    private static final double CRITICAL_LEVEL = 500.0;

    public StationService(StationRepository stationRepo, MeasurementRepository measurementRepo) {
        this.stationRepo = stationRepo;
        this.measurementRepo = measurementRepo;
    }

    public List<Station> getAllStations() { return stationRepo.findAll(); }

    public Station getStation(String id) { return stationRepo.findById(id); }

    public void addMeasurement(Measurement m) {
        if (m.getWaterLevel() >= CRITICAL_LEVEL) {
            m.setWarningLevel(WarningLevel.CRITICAL);
        } else if (m.getWaterLevel() >= WARN_LEVEL) {
            m.setWarningLevel(WarningLevel.HARD);
        } else {
            m.setWarningLevel(WarningLevel.NORMAL);
        }
        measurementRepo.save(m);
    }

    public Measurement getCurrentMeasurement(String stationId) {
        return measurementRepo.findLatestByStationId(stationId);
    }

    public List<Measurement> getMeasurements(String stationId, LocalDateTime from, LocalDateTime to, int limit) {
        return measurementRepo.findByStationId(stationId).stream()
                .filter(m -> (from == null || !m.getTimestamp().isBefore(from)) &&
                        (to == null || !m.getTimestamp().isAfter(to)))
                .limit(limit)
                .toList();
    }

    public List<Measurement> getMeasurementsByWarning(WarningLevel warning) {
        return measurementRepo.findAll().stream()
                .filter(m -> m.getWarningLevel() == warning)
                .toList();
    }

    public Statistics getStatistics(String stationId) {
        List<Measurement> list = measurementRepo.findByStationId(stationId);
        if (list.isEmpty()) {
            return new Statistics(0, 0, 0, 0, 0, 0, 0, 0);
        }

        double min = list.stream().mapToDouble(Measurement::getWaterLevel).min().orElse(0);
        double max = list.stream().mapToDouble(Measurement::getWaterLevel).max().orElse(0);
        double avgWater = list.stream().mapToDouble(Measurement::getWaterLevel).average().orElse(0);
        double avgFlow = list.stream().mapToDouble(Measurement::getFlowRate).average().orElse(0);
        double sumPrecip = list.stream().mapToDouble(Measurement::getPrecipitation).sum();

        int warnings = (int) list.stream().filter(m -> m.getWarningLevel() == WarningLevel.HARD).count();
        int critical = (int) list.stream().filter(m -> m.getWarningLevel() == WarningLevel.CRITICAL).count();

        return new Statistics(min, max, avgWater, avgFlow, sumPrecip, list.size(), warnings, critical);
    }
}