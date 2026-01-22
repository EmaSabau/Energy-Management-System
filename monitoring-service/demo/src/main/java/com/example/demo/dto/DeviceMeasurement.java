package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DeviceMeasurement {
    @JsonProperty("timestamp")
    public String timestamp;

    @JsonProperty("device_id")
    public String deviceId;

    @JsonProperty("measurement_value")
    public double measurementValue;

    public LocalDateTime getParsedTimestamp() {
        return LocalDateTime.parse(this.timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}