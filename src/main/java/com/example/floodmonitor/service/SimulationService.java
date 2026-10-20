package com.example.floodmonitor.service;

import com.example.floodmonitor.model.Measurement;
import com.example.floodmonitor.repository.StationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@ConditionalOnProperty(name = "simulation.enabled", havingValue = "true", matchIfMissing = true)
public class SimulationService {
    private final StationService stationService;
    private final StationRepository stationRepository;
    private final Random random = new Random();

    public SimulationService(StationService stationService, StationRepository stationRepository) {
        this.stationService = stationService;
        this.stationRepository = stationRepository;
        stationRepository.findAll().forEach(s -> generateMeasurement(s.getId()));
    }

    @Scheduled(fixedRateString = "${simulation.interval}")
    public void simulate() {
        stationRepository.findAll().forEach(s -> generateMeasurement(s.getId()));
    }

    private void generateMeasurement(String stationId) {
        Measurement m = new Measurement();
        m.setStationId(stationId);
        m.setTimestamp(LocalDateTime.now());
        m.setWaterLevel(200 + random.nextDouble() * 400);
        m.setFlowRate(10 + random.nextDouble() * 50);
        m.setPrecipitation(random.nextDouble() * 10);
        stationService.addMeasurement(m);
    }
}