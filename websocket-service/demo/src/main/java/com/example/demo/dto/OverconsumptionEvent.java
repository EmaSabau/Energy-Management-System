package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public class OverconsumptionEvent {

    @JsonProperty("device_id")
    private UUID deviceId;

    @JsonProperty("device_name")
    private String deviceName;

    @JsonProperty("username")
    private String username;

    @JsonProperty("current_consumption")
    private double currentConsumption;

    @JsonProperty("max_consumption")
    private double maxConsumption;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    public OverconsumptionEvent() {}

    public OverconsumptionEvent(UUID deviceId, String deviceName, String username,
                                double currentConsumption, double maxConsumption) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.username = username;
        this.currentConsumption = currentConsumption;
        this.maxConsumption = maxConsumption;
        this.timestamp = LocalDateTime.now();
    }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getCurrentConsumption() { return currentConsumption; }
    public void setCurrentConsumption(double currentConsumption) { this.currentConsumption = currentConsumption; }

    public double getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(double maxConsumption) { this.maxConsumption = maxConsumption; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
