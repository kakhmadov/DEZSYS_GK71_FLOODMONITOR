package com.example.floodmonitor.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.LocalDateTime;

@JacksonXmlRootElement(localName = "measurement")
public class Measurement {
    private String stationId;
    private LocalDateTime timestamp;
    private double waterLevel;
    private double flowRate;
    private double precipitation;
    private WarningLevel warningLevel;

    public Measurement() {}

    // Getter und Setter
    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public double getWaterLevel() { return waterLevel; }
    public void setWaterLevel(double waterLevel) { this.waterLevel = waterLevel; }
    public double getFlowRate() { return flowRate; }
    public void setFlowRate(double flowRate) { this.flowRate = flowRate; }
    public double getPrecipitation() { return precipitation; }
    public void setPrecipitation(double precipitation) { this.precipitation = precipitation; }
    public WarningLevel getWarningLevel() { return warningLevel; }
    public void setWarningLevel(WarningLevel warningLevel) { this.warningLevel = warningLevel; }
}