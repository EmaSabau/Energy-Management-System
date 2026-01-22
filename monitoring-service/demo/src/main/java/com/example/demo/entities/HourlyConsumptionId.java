package com.example.demo.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class HourlyConsumptionId implements Serializable {

    private UUID deviceId;
    private OffsetDateTime hourStart;

    public HourlyConsumptionId() {}

    public HourlyConsumptionId(UUID deviceId, OffsetDateTime hourStart) {
        this.deviceId = deviceId;
        this.hourStart = hourStart;
    }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public OffsetDateTime getHourStart() { return hourStart; }
    public void setHourStart(OffsetDateTime hourStart) { this.hourStart = hourStart; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HourlyConsumptionId that = (HourlyConsumptionId) o;
        return Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(hourStart, that.hourStart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, hourStart);
    }
}