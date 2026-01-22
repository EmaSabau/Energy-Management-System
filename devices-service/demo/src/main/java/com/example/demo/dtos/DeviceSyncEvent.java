package com.example.demo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceSyncEvent {

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("max_consumption")
    private Integer maxConsumption;

    @JsonProperty("owner_id")
    private String ownerId;

    // Constructors
    public DeviceSyncEvent() {}

    public DeviceSyncEvent(String eventType, String deviceId) {
        this.eventType = eventType;
        this.deviceId = deviceId;
    }

    public DeviceSyncEvent(String eventType, String deviceId, String name,
                           Integer maxConsumption, String ownerId) {
        this.eventType = eventType;
        this.deviceId = deviceId;
        this.name = name;
        this.maxConsumption = maxConsumption;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(Integer maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "DeviceSyncEvent{" +
                "eventType='" + eventType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", name='" + name + '\'' +
                ", maxConsumption=" + maxConsumption +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}