package com.example.floodmonitor.model;

public class Statistics {
    private double minWaterLevel;
    private double maxWaterLevel;
    private double avgWaterLevel;
    private double avgFlowRate;
    private double sumPrecipitation;
    private int measurementCount;
    private int warningCount;
    private int criticalWarningCount;

    public Statistics(double minWaterLevel, double maxWaterLevel, double avgWaterLevel,
                      double avgFlowRate, double sumPrecipitation, int measurementCount,
                      int warningCount, int criticalWarningCount) {
        this.minWaterLevel = minWaterLevel;
        this.maxWaterLevel = maxWaterLevel;
        this.avgWaterLevel = avgWaterLevel;
        this.avgFlowRate = avgFlowRate;
        this.sumPrecipitation = sumPrecipitation;
        this.measurementCount = measurementCount;
        this.warningCount = warningCount;
        this.criticalWarningCount = criticalWarningCount;
    }

    public double getMinWaterLevel() { return minWaterLevel; }
    public double getMaxWaterLevel() { return maxWaterLevel; }
    public double getAvgWaterLevel() { return avgWaterLevel; }
    public double getAvgFlowRate() { return avgFlowRate; }
    public double getSumPrecipitation() { return sumPrecipitation; }
    public int getMeasurementCount() { return measurementCount; }
    public int getWarningCount() { return warningCount; }
    public int getCriticalWarningCount() { return criticalWarningCount; }
}